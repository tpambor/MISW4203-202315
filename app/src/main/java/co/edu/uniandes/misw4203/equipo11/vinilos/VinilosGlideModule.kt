package co.edu.uniandes.misw4203.equipo11.vinilos

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class VinilosGlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled() = false
}
