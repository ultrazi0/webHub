import { useCallback, useEffect, useState } from "react";

interface Layout {
    buttons: Array<string>,
    axes: Array<string>,
}

const XBOXLayout: Layout = {
    buttons: [
        "A",
        "B",
        "X",
        "Y",
        "LB",
        "RB",
        "LT",
        "RT",
        "Back",
        "Start",
        "LS",
        "RS",
        "DPadUp",
        "DPadDown",
        "DPadLeft",
        "DPadRight",
        "XBox",
    ],
    axes: [
        "LeftStickX",
        "-LeftStickY",
        "RightStickX",
        "-RightStickY",
        "RightTrigger",
        "LeftTrigger",
    ],
};


type ButtonState = {
    [key in typeof XBOXLayout.buttons[number]]: boolean;
};

type AxesState = {
    [key in typeof XBOXLayout.axes[number]]: number;
};

export default function useGamepad(index: number = 0, layout: Layout = XBOXLayout): [ Gamepad | null, ButtonState, AxesState ] {

    const [ gamepad, setGamepad ] = useState<Gamepad | null>(null);
    const [ buttonState, setButtonState ] = useState<ButtonState>(() => clearButtonState(layout));
    const [ axesState, setAxesState] = useState<AxesState>(() => clearAxesState(layout));

    const updateGamepadState = useCallback((gamepad: Gamepad) => {

        const newButtonState: ButtonState = {};
        gamepad.buttons.forEach((button, index) => {
            const buttonName: string | undefined = layout.buttons[index];
            if (!buttonName) {
                console.error("Unknown button at index " + index);
                return;
            }
            newButtonState[buttonName] = button.pressed;
        });

        const newAxesState: AxesState = {};
        gamepad.axes.forEach((axis, index) => {
            const axisName: string | undefined = layout.axes[index];
            if (!axisName) {
                console.error("Unknown axis at index " + index);
                return;
            }
            newAxesState[axisName] = axis;
        });

        setButtonState(newButtonState);
        setAxesState(newAxesState);
    }, [ layout ]);

    const updateGamepad = useCallback(() => {
        const gamepad = navigator.getGamepads()[index];

        if (gamepad) {
            // gamepad exists
            setGamepad(gamepad);
            updateGamepadState(gamepad);
        } else {
            setGamepad(null);
            setButtonState(() => clearButtonState(layout));
            setAxesState(() => clearAxesState(layout));
        }
    }, [ index, layout, updateGamepadState ]);

    useEffect(() => {

        window.addEventListener("gamepadconnected", updateGamepad);
        window.addEventListener("gamepaddisconnected", updateGamepad);

        const interval = gamepad ? setInterval(updateGamepad, 100) : null;

        return () => {
            window.removeEventListener("gamepadconnected", updateGamepad);
            window.removeEventListener("gamepaddisconnected", updateGamepad);
            clearInterval(interval);
        };
    }, [ gamepad, updateGamepad ]);

    return [ gamepad, buttonState, axesState ];
}

const clearButtonState = (layout: Layout) =>
    layout.buttons
        .reduce((buttonState, currentValue) => {
            buttonState[currentValue] = false;
            return buttonState;
            }, {} as ButtonState);

const clearAxesState = (layout: Layout) =>
    layout.axes
        .reduce((axesState, currentValue) => {
            axesState[currentValue] = 0;
            return axesState;
        }, {} as AxesState);
