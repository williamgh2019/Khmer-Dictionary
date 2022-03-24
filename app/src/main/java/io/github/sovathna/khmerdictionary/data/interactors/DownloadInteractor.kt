package io.github.sovathna.khmerdictionary.data.interactors

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.sovathna.khmerdictionary.config.Const
import io.github.sovathna.khmerdictionary.domain.SplashService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject

@ViewModelScoped
class DownloadInteractor @Inject constructor(
    private val service: SplashService,
    private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val BYTE_TO_MB = 1_000_000.0
    }

    sealed interface Result {
        data class Downloading(val read: Double, val size: Double) : Result
        object Done : Result
        data class Error(val error: Throwable) : Result
    }

    fun downloadFlow(): Flow<Result> {

        val dbFile = context.getDatabasePath("dict.db")
        if (dbFile.exists()) {
            return flowOf(Result.Done)
        }

        var dbOutStream: FileOutputStream? = null
        var inStream: InputStream? = null
        var zipInStream: ZipInputStream? = null
        return flow {
            try {
                dbFile?.parentFile?.let { if (!it.exists()) it.mkdirs() }
                dbOutStream = FileOutputStream(dbFile)
                inStream = service.downloadDatabase(Const.DB_URL).byteStream()
                zipInStream = ZipInputStream(inStream)
                val entry = zipInStream!!.nextEntry
                val reader = ByteArray(4096)
                var totalRead = 0L
                var last = System.currentTimeMillis()
                val size = entry.compressedSize / BYTE_TO_MB
                val scale = entry.size.toDouble() / entry.compressedSize
                while (true) {
                    currentCoroutineContext().ensureActive()
                    val read = zipInStream!!.read(reader)
                    if (read == -1) break
                    dbOutStream!!.write(reader, 0, read)
                    totalRead += read
                    val current = System.currentTimeMillis()
                    if (last + 500 <= current) {
                        val tmp = totalRead / scale / BYTE_TO_MB
                        Timber.d("downloading: $tmp/$size")
                        emit(Result.Downloading(tmp, size))
                        last = current
                    }
                }
                dbOutStream!!.flush()
                Timber.d("download done: $size")
                emit(Result.Done)
            } finally {
                zipInStream?.closeEntry()
                zipInStream?.close()
                inStream?.close()
                dbOutStream?.close()
            }
        }.flowOn(ioDispatcher)
            .catch {
                Timber.e(it)
                dbFile?.delete()
                emit(Result.Error(it))
            }
    }


}