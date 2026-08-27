/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.lifecycle

import io.mockk.mockk
import mozilla.components.browser.state.action.BrowserAction
import mozilla.components.browser.state.action.EngineAction
import mozilla.components.browser.state.state.BrowserState
import mozilla.components.browser.state.state.createTab
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.EngineSession
import mozilla.components.lib.state.Middleware
import mozilla.components.lib.state.Store
import org.junit.Assert.assertEquals
import org.junit.Test

class BackgroundTabSuspenderTest {
    @Test
    fun `WHEN the application stops THEN suspend every active regular tab`() {
        val activeTab = createTab(
            url = "https://example.com/active",
            id = "active",
            engineSession = mockk<EngineSession>(relaxed = true),
        )
        val activePrivateTab = createTab(
            url = "https://example.com/private",
            id = "private",
            private = true,
            engineSession = mockk<EngineSession>(relaxed = true),
        )
        val alreadySuspendedTab = createTab(
            url = "https://example.com/suspended",
            id = "suspended",
        )
        val actions = mutableListOf<BrowserAction>()
        val store = BrowserStore(
            initialState = BrowserState(
                tabs = listOf(activeTab, activePrivateTab, alreadySuspendedTab),
            ),
            middleware = listOf(actionListenerMiddleware(actions)),
        )

        BackgroundTabSuspender(store).onStop(mockk())

        assertEquals(
            listOf("active", "private"),
            actions.filterIsInstance<EngineAction.SuspendEngineSessionAction>().map { it.tabId },
        )
    }

    private fun actionListenerMiddleware(
        actions: MutableList<BrowserAction>,
    ): Middleware<BrowserState, BrowserAction> =
        object : Middleware<BrowserState, BrowserAction> {
            override fun invoke(
                context: Store<BrowserState, BrowserAction>,
                next: (BrowserAction) -> Unit,
                action: BrowserAction,
            ) {
                actions.add(action)
                next(action)
            }
        }
}
