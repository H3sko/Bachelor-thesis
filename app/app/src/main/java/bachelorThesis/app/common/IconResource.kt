package bachelorThesis.app.common

import bachelorThesis.app.R


sealed class IconResource(val id: Int) {
    // TODO
    object Route: IconResource(R.mipmap.ic_route)
    object Place : IconResource(R.mipmap.ic_place)
    object Polygon : IconResource(R.mipmap.ic_polygon)
    object Airtag : IconResource(R.mipmap.ic_airtag)
}