package pers.lizechao.android_lib.utils

/**
 * Created by
 * ********************************************************************************
 * #         ___                     ________                ________             *
 * #       |\  \                   |\_____  \              |\   ____\             *
 * #       \ \  \                   \|___/  /|             \ \  \___|             *
 * #        \ \  \                      /  / /              \ \  \                *
 * #         \ \  \____                /  /_/__              \ \  \____           *
 * #          \ \_______\             |\________\             \ \_______\         *
 * #           \|_______|              \|_______|              \|_______|         *
 * #                                                                              *
 * ********************************************************************************
 * Date: 2020-02-07
 * Time: 14:59
 */
sealed class Either<out L, out R> {
    interface Visitor0<in L, in R, O> {
        fun visitLeft(l: L): O
        fun visitRight(l: R): O
    }

    interface Visitor1<in L, in R, I, O> {
        fun visitLeft(l: L, input: I): O
        fun visitRight(r: R, input: I): O
    }

    interface Visitor2<in L, in R, I1, I2, O> {
        fun visitLeft(l: L, i1: I1, i2: I2): O
        fun visitRight(r: R, i1: I1, i2: I2): O
    }

    interface Visitor3<in L, in R, I1, I2, I3, O> {
        fun visitLeft(l: L, i1: I1, i2: I2, i3: I3): O
        fun visitRight(r: R, i1: I1, i2: I2, i3: I3): O
    }

    abstract fun <O> accept(visitor: Visitor0<L, R, O>): O
    abstract fun <I, O> accept(visitor: Visitor1<L, R, I, O>, input: I): O
    abstract fun <I1, I2, O> accept(visitor: Visitor2<L, R, I1, I2, O>, i1: I1, i2: I2): O
    abstract fun <I1, I2, I3, O> accept(visitor: Visitor3<L, R, I1, I2, I3, O>, i1: I1, i2: I2, i3: I3): O

    companion object {
        @JvmStatic
        fun <L, R> left(l: L): Either<L, R> = Left(l)

        @JvmStatic
        fun <L, R> right(r: R): Either<L, R> = Right(r)
    }
}

data class Left<out L>(val value: L) : Either<L, Nothing>() {
    override fun <O> accept(visitor: Visitor0<L, Nothing, O>): O = visitor.visitLeft(value)
    override fun <I, O> accept(visitor: Visitor1<L, Nothing, I, O>, input: I): O = visitor.visitLeft(value, input)
    override fun <I1, I2, O> accept(visitor: Visitor2<L, Nothing, I1, I2, O>, i1: I1, i2: I2): O = visitor.visitLeft(value, i1, i2)
    override fun <I1, I2, I3, O> accept(visitor: Visitor3<L, Nothing, I1, I2, I3, O>, i1: I1, i2: I2, i3: I3): O = visitor.visitLeft(value, i1, i2, i3)
}

data class Right<out R>(val value: R) : Either<Nothing, R>() {
    override fun <O> accept(visitor: Visitor0<Nothing, R, O>): O = visitor.visitRight(value)
    override fun <I, O> accept(visitor: Visitor1<Nothing, R, I, O>, input: I): O = visitor.visitRight(value, input)
    override fun <I1, I2, O> accept(visitor: Visitor2<Nothing, R, I1, I2, O>, i1: I1, i2: I2): O = visitor.visitRight(value, i1, i2)
    override fun <I1, I2, I3, O> accept(visitor: Visitor3<Nothing, R, I1, I2, I3, O>, i1: I1, i2: I2, i3: I3): O = visitor.visitRight(value, i1, i2, i3)
}

inline fun <L, R, U> Either<L, R>.fold(left: (L) -> U, right: (R) -> U): U = when (this) {
    is Left -> left(value)
    is Right -> right(value)
}

inline val <V> Either<V, V>.join get() = fold({ it }, { it })

inline fun <L, R, L1, R1> Either<L, R>.bimap(left: (L) -> L1, right: (R) -> R1): Either<L1, R1> = fold({ Either.left(left(it)) }, { Either.right(right(it)) })

inline val <L, R> Either<L, R>.isRight: Boolean
    get() = when (this) {
        is Left -> false
        is Right -> true
    }


inline val <L, R> Either<L, R>.isLeft: Boolean get() = !isRight

inline fun <L, R> Either<L, R>.getOr(block: L.() -> R) = fold(block, { it })

inline fun <R> Try<R>.orTry(block: Throwable.() -> R): Try<R> = Try { getOr { block() } }

inline fun <A, B, C, D> ((Either<A, B>) -> D).foldOver(input: Either<A, C>, transform: (C) -> D): D = input.fold({ this(Left(it)) }, { transform(it) })

inline fun <L, R, U> Either<L, R>.lmap(transform: (L) -> U): Either<U, R> = when (this) {
    is Right -> this
    is Left -> Left(transform(value))
}

inline infix fun <L, R, U> Either<L, R>.map(transform: (R) -> U): Either<L, U> = when (this) {
    is Right -> Right(transform(value))
    is Left -> this
}

inline infix fun <L, R, U> Either<L, R>.flatMap(point: (R) -> Either<L, U>) = when (this) {
    is Left -> this
    is Right -> point(value)
}

inline val <L, R> Either<L, R>.swap: Either<R, L>
    get() = when (this) {
        is Right -> Left(value)
        is Left -> Right(value)
    }

inline val <T> Either<T, T>.value: T get() = fold({ it }, { it })

inline val <R> Try<R>.option: R? get() = (this as? Right)?.value

inline fun <L, R> Either<L, R>.forEach(block: (R) -> Unit) = (this as? Right)?.value?.let(block)

typealias Try<R> = Either<Throwable, R>

inline fun <R> Try(block: () -> R): Try<R> = try {
    Right(block())
} catch (e: Throwable) {
    Left(e)
}

inline fun <R, U> Try<R>.flatMapTry(block: (R) -> U) = flatMap { Try { block(it) } }

inline val <L, R> Either<L, Either<L, R>>.joinRight: Either<L, R>
    get() = when (this) {
        is Left -> this
        is Right -> value
    }

inline val <L, R> Either<Either<L, R>, R>.joinLeft: Either<L, R>
    get() = when (this) {
        is Left -> value
        is Right -> this
    }


inline val <R> Try<R>.get: R
    get() = when (this) {
        is Left -> throw value
        is Right -> value
    }

inline fun <L, R : Any> R?.toRight(left: () -> L): Either<L, R> = this?.let(::Right) ?: Left(left())