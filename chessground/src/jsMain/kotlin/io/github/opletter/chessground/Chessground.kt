@file:JsModule("chessground")
@file:JsNonModule

package io.github.opletter.chessground

import org.w3c.dom.HTMLElement

external fun Chessground(element: HTMLElement, config: Config? = definedExternally): Api