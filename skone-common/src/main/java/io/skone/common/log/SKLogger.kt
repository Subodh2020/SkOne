@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.common.log

import io.skone.common.annotation.SKInternal

/**
 * Logging SPI for SKOne.
 *
 * Apps may install a custom implementation via [io.skone.SKOneConfig].
 * Do not hardcode Android Log calls in library modules when [SKLogger] is available.
 */
public interface SKLogger {
    /** Verbose log. */
    public fun v(tag: String, message: String, throwable: Throwable? = null)

    /** Debug log. */
    public fun d(tag: String, message: String, throwable: Throwable? = null)

    /** Info log. */
    public fun i(tag: String, message: String, throwable: Throwable? = null)

    /** Warning log. */
    public fun w(tag: String, message: String, throwable: Throwable? = null)

    /** Error log. */
    public fun e(tag: String, message: String, throwable: Throwable? = null)
}

/**
 * No-op logger used when logging is intentionally disabled.
 */
public object SKNoOpLogger : SKLogger {
    override fun v(tag: String, message: String, throwable: Throwable?): Unit = Unit
    override fun d(tag: String, message: String, throwable: Throwable?): Unit = Unit
    override fun i(tag: String, message: String, throwable: Throwable?): Unit = Unit
    override fun w(tag: String, message: String, throwable: Throwable?): Unit = Unit
    override fun e(tag: String, message: String, throwable: Throwable?): Unit = Unit
}

/**
 * Default logger that writes to Android's [android.util.Log].
 *
 * **Internal implementation** — not intended for application use. Install a custom
 * [SKLogger] via [io.skone.SKOneConfig] instead of referencing this type directly.
 */
@SKInternal
public object SKDefaultLogger : SKLogger {
    override fun v(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            android.util.Log.v(tag, message, throwable)
        } else {
            android.util.Log.v(tag, message)
        }
    }

    override fun d(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            android.util.Log.d(tag, message, throwable)
        } else {
            android.util.Log.d(tag, message)
        }
    }

    override fun i(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            android.util.Log.i(tag, message, throwable)
        } else {
            android.util.Log.i(tag, message)
        }
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            android.util.Log.w(tag, message, throwable)
        } else {
            android.util.Log.w(tag, message)
        }
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            android.util.Log.e(tag, message, throwable)
        } else {
            android.util.Log.e(tag, message)
        }
    }
}

/**
 * Process-wide logging facade. Backed by the logger installed during SDK init,
 * or [SKDefaultLogger] before initialization.
 */
public object SKLog {
    @Volatile
    private var delegate: SKLogger = SKDefaultLogger

    /**
     * Installs the active logger.
     *
     * **Internal** — used by [io.skone.SKOne] during initialization. Applications should
     * pass a logger through [io.skone.SKOneConfig], not call this method directly.
     */
    @SKInternal
    public fun install(logger: SKLogger) {
        delegate = logger
    }

    /** @see SKLogger.v */
    public fun v(tag: String, message: String, throwable: Throwable? = null) {
        delegate.v(tag, message, throwable)
    }

    /** @see SKLogger.d */
    public fun d(tag: String, message: String, throwable: Throwable? = null) {
        delegate.d(tag, message, throwable)
    }

    /** @see SKLogger.i */
    public fun i(tag: String, message: String, throwable: Throwable? = null) {
        delegate.i(tag, message, throwable)
    }

    /** @see SKLogger.w */
    public fun w(tag: String, message: String, throwable: Throwable? = null) {
        delegate.w(tag, message, throwable)
    }

    /** @see SKLogger.e */
    public fun e(tag: String, message: String, throwable: Throwable? = null) {
        delegate.e(tag, message, throwable)
    }

    /**
     * Resets to [SKDefaultLogger].
     *
     * **Internal test hook** — not part of the application SDK.
     */
    @SKInternal
    public fun resetForTest() {
        delegate = SKDefaultLogger
    }
}
