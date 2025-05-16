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

export type Callbacks<T extends Layout> = {
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

export default function useGamepad<T extends Layout>(layout: T, callbacks: Callbacks<T>, index: number = 0, options: Options = defaultOptions): [ Pick<Gamepad, "id" | "index" | "connected"> | null, ButtonState<T>, AxesState<T> ] {
    
    const [ gamepad, setGamepad ] = useState<Pick<Gamepad, "id" | "index" | "connected"> | null>(null);
    const [ buttonState, setButtonState ] = useState<ButtonState<T>>(() => clearButtonState(layout));
    const [ axesState, setAxesState] = useState<AxesState<T>>(() => clearAxesState(layout));

    const updateButtonState = useCallback((newButtonState: ButtonState<T>) =>
        setButtonState(oldButtonState => {
            layout.buttons.forEach((buttonName: keyof ButtonState<T>) => {
                if (oldButtonState[buttonName] !== newButtonState[buttonName]) {
                    callbacks[buttonName]?.(newButtonState[buttonName]);
                }
            });
            return newButtonState;
        }), [ layout.buttons, callbacks ]);
    
    const updateAxesState = useCallback((newAxesState: AxesState<T>) =>
        setAxesState(oldAxesState => {
            layout.axes.forEach((axisName: keyof AxesState<T>) => {
                if (Math.abs(oldAxesState[axisName] - newAxesState[axisName]) > options.threshold) {
                    callbacks[axisName]?.(newAxesState[axisName]);
                }
            });
            return newAxesState;       
        }), [ layout.axes, options.threshold, callbacks ]);

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
        const gamepad = navigator.getGamepads()[index];

        if (gamepad) {
            // gamepad exists
            setGamepad({
                id: gamepad.id,
                index: gamepad.index,
                connected: gamepad.connected,
            });
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
