tasks {
    register("pnpmInstall", Exec::class) {
        commandLine("pnpm", "install")
    }

    register("pnpmBuild", Exec::class) {
        commandLine("pnpm", "run", "build")
        dependsOn("pnpmInstall")
        outputs.upToDateWhen { false }
    }

    register("pnpmDevelop", Exec::class) {
        commandLine("pnpm", "run", "dev")
        dependsOn("pnpmInstall")
    }
}
