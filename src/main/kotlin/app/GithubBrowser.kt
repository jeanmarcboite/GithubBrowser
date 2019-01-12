package app

import view.LoginScreen
import javafx.stage.Stage
import tornadofx.App

class GithubBrowser: App(LoginScreen::class, Styles::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1060.0
    }
}