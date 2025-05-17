import { useCallback, useEffect, useState } from "react";

type Options = {
    deadZone: number;
    threshold: number;
}

const defaultOptions: Options = {
    deadZone: 0.2,
    threshold: 0.1,
};

export interface Layout {
    buttons: readonly string[],
    axes: readonly string[],
}

export const XBOXLayout = {
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
} as const;

export type onChangeCallbacks<T extends Layout> = {
    [key in T["buttons"][number]]?: (pressed: boolean) => void;
} & {
    [key in T["axes"][number]]?: (value: number) => void;
};

type ButtonState<T extends Layout> = {
    [key in T["buttons"][number]]: boolean;
};

type AxesState<T extends Layout> = {
    [key in T["axes"][number]]: number;
};

type IdentifiableGamepad = Pick<Gamepad, "id" | "index" | "connected"> | null;

export default function useGamepad<T extends Layout>(layout: T, callbacks: onChangeCallbacks<T>, index: number = 0, options: Options = defaultOptions): [ IdentifiableGamepad, ButtonState<T>, AxesState<T> ] {
    
    const [ gamepad, setGamepad ] = useState<IdentifiableGamepad>(null);
    const [ buttonState, setButtonState ] = useState<ButtonState<T>>(() => clearButtonState(layout));
    const [ axesState, setAxesState] = useState<AxesState<T>>(() => clearAxesState(layout));

    const updateButtonState = useCallback((newButtonState: ButtonState<T>) => {
        let changed: boolean = false;
        layout.buttons.forEach((buttonName: keyof ButtonState<T>) => {
            if (buttonState[buttonName] !== newButtonState[buttonName]) {
                changed = true;
                callbacks[buttonName]?.(newButtonState[buttonName]);
            }
        });
        
        if (changed) setButtonState(newButtonState);
    }, [ layout.buttons, buttonState, callbacks ]);
    
    const updateAxesState = useCallback((newAxesState: AxesState<T>) => {
        let changed: boolean = false;
        layout.axes.forEach((axisName: keyof AxesState<T>) => {
            if (Math.abs(axesState[axisName] - newAxesState[axisName]) > options.threshold) {
                changed = true;
                callbacks[axisName]?.(newAxesState[axisName]);
            }
        });
        
        if (changed) setAxesState(newAxesState);
    }, [ layout.axes, axesState, options.threshold, callbacks ]);

    const updateGamepadState = useCallback((gamepad: Gamepad) => {

        const newButtonState = gamepad.buttons
            .reduce((buttonState, button, index) => {
                const buttonName: keyof ButtonState<T> | undefined = layout.buttons[index];
                if (!buttonName) {
                    console.error("Unknown button at index " + index);
                    return buttonState;
                }
                buttonState[buttonName] = button.pressed;
                return buttonState;
            }, {} as ButtonState<T>);

        const newAxesState = gamepad.axes
            .reduce((axesState, axis, index) => {
                const axisName: keyof AxesState<T> | undefined = layout.axes[index];
                if (!axisName) {
                    console.error("Unknown axis at index " + index);
                    return axesState;
                }
                axesState[axisName] = axis;
                return axesState;
            }, {} as AxesState<T>);

        updateButtonState(newButtonState);
        updateAxesState(newAxesState);
    }, [ layout, updateButtonState, updateAxesState ]);

    const updateGamepad = useCallback(() => {
        const newGamepad = navigator.getGamepads()[index];

        if (newGamepad) {
            // gamepad exists
            if (!gamepadsAreEqual(gamepad, newGamepad)) {
                setGamepad({
                    id: newGamepad.id,
                    index: newGamepad.index,
                    connected: newGamepad.connected,
                });
            }
            updateGamepadState(newGamepad);
        } else {
            if (gamepad != null) {
                setGamepad(null);
                setButtonState(() => clearButtonState(layout));
                setAxesState(() => clearAxesState(layout));
            }
        }
    }, [ gamepad, index, layout, updateGamepadState ]);

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

function clearButtonState<T extends Layout>(layout: T) {
    return layout.buttons
        .reduce((buttonState, currentValue: keyof ButtonState<T>) => {
            buttonState[currentValue] = false;
            return buttonState;
        }, {} as ButtonState<T>);
}

function clearAxesState<T extends Layout>(layout: T) {
    return layout.axes
        .reduce((axesState, currentValue: keyof AxesState<T>) => {
            axesState[currentValue] = 0;
            return axesState;
        }, {} as AxesState<T>);
}

function gamepadsAreEqual(gamepad1: IdentifiableGamepad | undefined, gamepad2: IdentifiableGamepad | undefined) {
    return gamepad1?.id === gamepad2?.id && gamepad1?.index === gamepad2?.index && gamepad1?.connected === gamepad2?.connected;
}
