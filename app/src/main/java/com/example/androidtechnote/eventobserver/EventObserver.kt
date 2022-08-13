package com.example.androidtechnote.eventobserver

import androidx.lifecycle.Observer

/**
 * 이벤트를 나타내는 LiveData를 통해 노출되는 데이터의 래퍼로 사용
 */
open class EventWrapper<out T>(private val content: T) {

    var hasBeenHandled = false // 단일 이벤트가 이미 실행됐는지를 판단하는 hasBeenHandled 변수
        private set // 외부 읽기는 허용하지만 쓰기는 허용하지 않음

    /**
     * 콘텐츠를 반환하고 다시 사용할 수 없도록 함 (화면 회전 등)
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) { // 이벤트가 이미 처리 되었다면
            null                     // null을 반환하고,
        } else {                     // 그렇지 않다면
            hasBeenHandled = true    // 이벤트가 처리되었다고 표시한 후에
            content                  // 값을 반환합니다.
        }
    }

    /**
     * 이벤트의 처리 여부에 상관 없이 값을 반환합니다.
     */
    fun peekContent(): T = content
}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<EventWrapper<T>> {
    override fun onChanged(event: EventWrapper<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}
