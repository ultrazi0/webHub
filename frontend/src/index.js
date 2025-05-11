import React, { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import { RouterProvider, createBrowserRouter } from "react-router-dom";
import ErrorPage from "./pages/ErrorPage";
import Root, { allRobotsLoader as robotsLoader, editRobotAction, addRobotAction, deleteRobotAction } from "./pages/Root";
import ControlPanel from "./pages/ControlPanel";
import NavigationBar, { logoutAction, userLoader } from "./components/NavigationBar";
import LoginPage, { loginAction, loginLoader } from "./pages/Login";
import RegisterPage, { registerAction } from "./pages/Register";
import UserPage, { loadUser } from "./pages/UserPage";
import EditUserPage, { editUserAction } from "./pages/EditUserPage";
import { deleteUserAction } from "./components/DeleteUserModal";

const router = createBrowserRouter([
  {
    path: "/",
    element: <NavigationBar />,
    loader: userLoader,
    errorElement: <ErrorPage />,
    children: [
      {
        errorElement: <ErrorPage />,
        children: [
          {
            index: true,
            element: <Root />,
            loader: robotsLoader,
            action: addRobotAction,
          },
          {
            path: "edit/:robotId",
            action: editRobotAction,
          },
          {
            path: "delete/:robotId",
            action: deleteRobotAction,
          },
          {
            path: "control-panel/:robotId",
            element: <ControlPanel />,
          },
          {
            path: "login",
            element: <LoginPage />,
            loader: loginLoader,
            action: loginAction,
          },
          {
            path: "logout",
            action: logoutAction,
          },
          {
            path: "register",
            element: <RegisterPage />,
            loader: loginLoader,
            action: registerAction,
          },
          {
            path: "user/:userId",
            element: <UserPage />,
            loader: loadUser,
            children: [
              {
                path: "delete",
                action: deleteUserAction,
              },
            ],
          },
          {
            path: "user/edit",
            element: <EditUserPage />,
            loader: loginLoader,
            action: editUserAction,
          },
        ],
      },
    ],
  },
]);

const root = createRoot(document.getElementById("root"));
root.render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>,
);
