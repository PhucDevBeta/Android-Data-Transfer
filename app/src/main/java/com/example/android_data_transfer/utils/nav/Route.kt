package com.example.android_data_transfer.utils.nav

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data object MainRoute : BaseRoute()

@Keep
@Serializable
data object PhotoTransferRoute : BaseRoute()

@Keep
@Serializable
data object VideoTransferRoute : BaseRoute()

@Keep
@Serializable
data object FilesTransferRoute : BaseRoute()

@Keep
@Serializable
data object AudioTransferRoute : BaseRoute()