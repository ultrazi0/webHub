import { createContext } from "react";
import { User } from "./types";

export const AuthenticationContext = createContext<User | null>(null);
