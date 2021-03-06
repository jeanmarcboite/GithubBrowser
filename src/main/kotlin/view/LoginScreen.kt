package view

import app.Styles
import app.Styles.Companion.errorMessage
import app.Styles.Companion.footer
import app.Styles.Companion.h1
import app.Styles.Companion.loginScreen
import app.Styles.Companion.newToGitHub
import app.Styles.Companion.successButton
import controller.GitHub
import javafx.geometry.Orientation.VERTICAL
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.layout.StackPane
import model.UserModel
import tornadofx.*
import tornadofx.FX.Companion.application

class LoginScreen : View("Sign in to GitHub") {
    val github: GitHub by inject()
    val model: UserModel by inject()

    val messageWrapper by cssid()
    val passwordField by cssid()

    override val root = vbox {
        addClass(loginScreen)

        label().addClass(Styles.logoIcon, Styles.icon, Styles.large)
        label(title).addClass(h1)
        stackpane().setId(messageWrapper)
        form {
            fieldset(labelPosition = VERTICAL) {
                field("Username or email address") {
                    textfield(model.login) {
                        required(message = "Enter your login name")
                    }
                }
                field("Password") {
                    passwordfield(model.password) {
                        setId(passwordField)
                        required(message = "Enter your password")
                    }
                }.forgotPasswordLink()
            }

            button("Sign in") {
                isDefaultButton = true
                addClass(successButton)
                action {
                    login()
                }
            }
        }
        hbox {
            addClass(newToGitHub)
            text("New to GitHub? ")
            hyperlink("Create an account.") {
                setOnAction {
                    application.hostServices.showDocument("https://github.com/join?source=login")
                }
            }
        }
        hbox {
            addClass(footer)
            label("TornadoFX Showcase Application")
        }
    }

    private fun Button.login() {
        // Temporarily change the text and opacity of the login button
        fun signalSigningIn() {
            properties["originalText"] = text
            text = "Signing in..."
            opacity = 0.5
        }

        // Reset the text and opacity
        fun signalSigningComplete() {
            text = properties["originalText"] as String
            opacity = 1.0
        }

        if (model.commit()) {
            signalSigningIn()

            runAsync {
                github.login(model.login.value, model.password.value)
            } ui { success ->
                signalSigningComplete()

                if (success) {
                    replaceWith(UserScreen::class, ViewTransition.Slide(0.3.seconds))
                } else {
                    loginFailed()
                }
            }
        }

    }


    /**
     * Locate the messageWrapper by it's CSS id and replace it's content
     * with a "login failed" error message. Then focus the password field.
     */
    private fun loginFailed() {
        root.select<StackPane>(messageWrapper).replaceChildren {
            hbox {
                addClass(errorMessage)
                label("Incorrect username or password.")
                spacer()
                button {
                    addClass(Styles.crossIcon, Styles.icon, Styles.small)
                    action {
                        this@hbox.removeFromParent()
                    }
                }
            }
        }

        root.select<PasswordField>(passwordField).requestFocus()
    }

    fun Field.forgotPasswordLink() {
        label.style { minWidth = 170.px }
        labelContainer.hyperlink("Forgot password?") {
            isFocusTraversable = false
            style { fontSize = 12.px }
        }
    }
}