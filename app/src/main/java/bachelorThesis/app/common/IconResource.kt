package bachelorThesis.app.common

import bachelorThesis.app.R


sealed class IconResource(val id: Int) {
    // TODO
    object Route: IconResource(R.mipmap.ic_route)
    object Place : IconResource(R.mipmap.ic_place)
}