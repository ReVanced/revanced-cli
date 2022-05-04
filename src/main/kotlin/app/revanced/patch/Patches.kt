package app.revanced.patch

import app.revanced.patches.Index

internal class Patches {
    internal companion object {
        // You may ask yourself, "why do this?".
        // We do it like this, because we don't want the Index class
        // to be loaded while the dependency hasn't been injected yet.
        // You can see this as "controlled class loading".
        // Whenever this class is loaded (because it is invoked), all the imports
        // will be loaded too. We don't want to do this until we've injected the class.
        internal fun loadPatches() = Index.patches
    }
}