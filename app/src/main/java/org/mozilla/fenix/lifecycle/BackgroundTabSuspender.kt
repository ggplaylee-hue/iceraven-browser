/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import mozilla.components.browser.state.action.EngineAction
import mozilla.components.browser.state.store.BrowserStore

/** Releases regular tab engine sessions as soon as the application leaves the foreground. */
class BackgroundTabSuspender(
    private val browserStore: BrowserStore,
) : DefaultLifecycleObserver {
    override fun onStop(owner: LifecycleOwner) {
        browserStore.state.tabs
            .filter { it.engineState.engineSession != null }
            .forEach { tab ->
                browserStore.dispatch(EngineAction.SuspendEngineSessionAction(tab.id))
            }
    }
}
