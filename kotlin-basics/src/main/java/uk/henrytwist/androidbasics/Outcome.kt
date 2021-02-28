package uk.henrytwist.androidbasics

sealed class Outcome<out T> {

    object Waiting : Outcome<Nothing>()

    class Success<T>(val data: T) : Outcome<T>()

    object Failure : Outcome<Nothing>()

    inline fun <O> map(transform: (T) -> O): Outcome<O> {

        return when (this) {

            is Waiting -> Waiting

            is Success -> success(transform(data))

            is Failure -> Failure
        }
    }

    inline fun <O> switchMap(transform: (T) -> Outcome<O>): Outcome<O> {

        return when (this) {

            is Waiting -> Waiting

            is Success -> transform(data)

            is Failure -> Failure
        }
    }

    inline fun ifSuccessful(then: (data: T) -> Unit) {

        if (this is Success) then(data)
    }
}

fun <T, O> Outcome<List<T>>.mapEach(transform: (T) -> O): Outcome<List<O>> {

    return map {

        it.map(transform)
    }
}

fun <T> Outcome<T>?.successOrNull() = if (this is Outcome.Success) data else null

fun waiting() = Outcome.Waiting

fun <T> success(data: T) = Outcome.Success(data)

fun failure() = Outcome.Failure

fun <T> T.asSuccess() = success(this)