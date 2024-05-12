package bachelorThesis.app.common

import bachelorThesis.app.R


sealed class IconResource(val id: Int) {
    // TODO
    object Route: IconResource(R.drawable.ic_route)
    object Place : IconResource(R.drawable.ic_place)
}