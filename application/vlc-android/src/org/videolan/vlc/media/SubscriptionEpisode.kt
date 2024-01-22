/*
 * ************************************************************************
 *  SubscriptionEpisode.kt
 * *************************************************************************
 * Copyright © 2022 VLC authors and VideoLAN
 * Author: Nicolas POMEPUY
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 * **************************************************************************
 *
 *
 */

package org.videolan.vlc.media

import android.view.View
import org.videolan.medialibrary.Tools
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.Subscription
import org.videolan.medialibrary.media.MediaWrapperImpl
import org.videolan.vlc.util.TextUtils
import org.videolan.vlc.util.TimeUtils

class SubscriptionEpisode(mw: MediaWrapper, private val subscriptions: List<Subscription>, var inPlayQueue: Boolean) : MediaWrapperImpl(mw.id, mw.uri.toString(), mw.time, mw.position, mw.length, mw.type, mw.title,
        mw.fileName, mw.artist, mw.genre, mw.album, mw.albumArtist,
        mw.width, mw.height, mw.artworkURL, mw.audioTrack, mw.spuTrack, mw.trackNumber,
        mw.discNumber, mw.lastModified, mw.seen, mw.isThumbnailGenerated,
        mw.isFavorite, mw.releaseYear, mw.isPresent, mw.insertionDate, mw.nbSubscriptions) {

    fun subsText() = TextUtils.separatedString(subscriptions.map { it.title }.toTypedArray())
    fun dateDuration() = (if (length == -1L) "???" else Tools.millisToString(length)) ?: "???"
    fun releaseDate(view: View, precise:Boolean) = TimeUtils.formatDateToNatural(view.context, releaseYear * 1000L, precise)
    override fun getArtworkMrl(): String {
        if (subscriptions.isEmpty()) return super.getArtworkMrl()
        return subscriptions[0].artworkMrl
    }

    fun summary() = if (description.isNullOrBlank()) description
            ?: ("<p>Lorem ipsum dolor sit amet, <a href=\"http://google.com\">consectetur adipiscing</a> elit. <b>Phasellus</b> ante metus, volutpat eget ultricies sit amet, sagittis ut justo. Praesent dignissim imperdiet tellus at malesuada. Integer tincidunt dui justo, vitae blandit tortor volutpat quis. Fusce aliquet, lectus quis suscipit interdum, augue enim elementum arcu, vitae egestas quam diam at magna. Sed rhoncus dui sed posuere elementum. Phasellus id nunc in ligula dictum facilisis eu sed dolor. Donec at urna sit amet est pulvinar mollis eget vel quam. Praesent sed iaculis mi. Mauris convallis nibh vel tortor congue, nec scelerisque libero fringilla. In ac orci eget massa maximus convallis. Vestibulum malesuada elit lacus, non viverra metus imperdiet eget. Aliquam eget sollicitudin enim, sed hendrerit lorem. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis sed ligula sagittis orci consectetur hendrerit.</p>" +
                    "<br/>http://google.com"+
                    "<br/>example@google.com"+
                    "<br/>@netflix"+
                    "<br/>Donec nec nibh eu est hendrerit pellentesque in eu est. Phasellus sodales ex in dolor ornare mattis. Nam rhoncus posuere consequat. Quisque lacinia, ante condimentum condimentum luctus, erat mi imperdiet libero, vel malesuada sem tellus non est. Nulla elementum, purus ut tempus scelerisque, nisi augue faucibus massa, in lacinia nisi justo a nisl. Morbi eget ligula in tellus ultricies ultricies. Nullam consequat mi vitae sodales faucibus. Aenean at ullamcorper orci, non interdum metus. Suspendisse in rutrum odio, non cursus libero. Vestibulum ut erat sollicitudin, commodo lorem eu, pellentesque tortor. Curabitur feugiat ex ut dui gravida, non fringilla mi venenatis.</p><p>" +
                    "Praesent sit amet sapien neque. Duis convallis hendrerit ornare. Mauris rutrum nulla non sem volutpat gravida. Suspendisse ultricies ante id enim congue, laoreet tristique nisl lacinia. Ut ultrices libero id velit bibendum mollis. Nam mauris urna, sagittis imperdiet lacus aliquam, suscipit venenatis orci. Phasellus lobortis mauris quis pellentesque congue. Maecenas tempor nisi a libero sollicitudin tristique. Vivamus mollis augue egestas massa elementum blandit. Fusce nec dictum ipsum.</p><p>" +
                    "Nunc augue justo, dictum in elementum vel, dignissim ac lectus. Duis at orci eget dui rutrum posuere quis vitae est. Morbi imperdiet mauris lobortis pretium ullamcorper. Aliquam suscipit justo tellus, sit amet elementum tortor elementum ac. Nam sem enim, venenatis vitae venenatis in, tempor id justo. Sed molestie nibh eget finibus volutpat. Donec ullamcorper porta enim, imperdiet ornare risus. Sed a augue semper, feugiat enim at, eleifend risus. Sed quis vestibulum tellus. Sed placerat massa luctus posuere ullamcorper. Donec finibus dui erat, ac luctus turpis imperdiet vitae. Curabitur vitae tristique elit. Curabitur odio nisl, accumsan eget orci vel, pulvinar ornare lacus.</p><p>" +
                    "Ut eget neque metus. Quisque sed lectus metus. Etiam eu elit tincidunt, volutpat elit sit amet, sodales turpis. Nam viverra nulla diam, eget facilisis purus vehicula non. Praesent ac elementum libero. Vestibulum sem ante, vestibulum vel iaculis ut, semper ut enim. Duis sit amet enim finibus, vestibulum ipsum sed, finibus ipsum. Maecenas justo felis, accumsan molestie vulputate sed, semper nec leo. Ut vel sollicitudin sapien, vitae tempus sem. Nulla cursus aliquet dolor sed mollis. Aliquam sem ante, fringilla sit amet quam elementum, condimentum finibus purus. Fusce in accumsan nibh, quis efficitur dolor. Donec sed urna nulla. Etiam faucibus pretium posuere.</p><p>" +
                    "Donec eleifend imperdiet magna, sed blandit justo congue et. Sed tristique tempus magna, id ultricies arcu commodo id. Suspendisse tortor eros, tincidunt a nisi id, consequat interdum magna. Vestibulum ut lorem ut lorem consequat rutrum. Integer turpis erat, cursus eu ex vel, ornare feugiat dolor. Sed varius sagittis massa, et pellentesque enim porttitor venenatis. Suspendisse porta arcu in libero accumsan mattis. Vestibulum sed lacinia eros, ut lacinia nunc. Suspendisse consectetur quis diam in iaculis. Fusce vehicula sem lorem, quis aliquet diam varius non. Morbi nisl lorem, pharetra et blandit et, pharetra vel leo. Donec libero enim, pellentesque non erat a, efficitur congue massa. Vivamus ornare lectus in felis tempus dictum. Sed nisl felis, molestie vel neque sed, molestie hendrerit urna. Etiam mollis dui nisl, ac facilisis velit faucibus vitae. Integer ipsum neque, sagittis ac placerat vitae, aliquam ut justo.</p><p>" +
                    "Suspendisse potenti. Vivamus eget nisl vel metus tristique placerat nec id elit. Ut quis enim porta, congue tellus non, vulputate lorem. Sed sed leo vulputate, bibendum nulla non, sagittis orci. Vivamus mi velit, tristique commodo viverra in, lacinia at turpis. Nunc at risus facilisis, hendrerit ligula quis, auctor nunc. Nullam et tempor eros. Nullam in tellus et lorem efficitur consequat nec nec massa. In tempus, tellus vel fermentum scelerisque, nunc massa tempor orci, quis lacinia tortor eros eget elit. Cras blandit imperdiet mi, eu porttitor mi laoreet eu. Proin ullamcorper gravida dapibus.</p><p>" +
                    "Nunc viverra mattis porttitor. Phasellus sollicitudin nisl a magna ultricies, sit amet interdum lacus ullamcorper. Nam mollis, sapien vel vestibulum sollicitudin, erat urna maximus libero, quis feugiat nisi augue in felis. Nunc dapibus et est sed aliquam. Nam molestie felis elit, in sodales turpis bibendum sit amet. Aliquam mollis mauris eu diam suscipit tristique. Nam sed porttitor nulla, ut iaculis libero. Cras justo turpis, malesuada venenatis augue sed, lacinia laoreet eros. Donec feugiat rutrum nisi, at egestas est.</p><p>" +
                    "Cras et maximus arcu, id pharetra lacus. Mauris sed ante ante. Ut sollicitudin libero sed ornare mollis. Vestibulum sagittis erat dolor. In nec leo ultrices sapien semper sodales. Suspendisse dapibus enim vel purus commodo porta. Cras in consequat leo, ut convallis nunc. Curabitur ac mi vel enim iaculis consequat quis dictum diam. Phasellus purus nunc, viverra in placerat vitae, convallis in metus. Nam mollis non purus ut cursus. Suspendisse malesuada, odio nec congue viverra, ipsum lorem consectetur magna, a vulputate libero libero ut dolor. Donec ultrices, augue eget dictum feugiat, enim dolor faucibus erat, nec faucibus massa metus id mi. Interdum et malesuada fames ac ante ipsum primis in faucibus. Quisque et libero sit amet elit molestie feugiat. In et porta magna.</p><p>" +
                    "Aenean quis pulvinar magna. Vestibulum sit amet augue ante. Nulla facilisi. Proin auctor id magna sit amet pulvinar. In id tellus accumsan, euismod arcu eget, sollicitudin purus. Fusce vitae mauris ultricies felis rhoncus lacinia nec aliquam odio. Fusce aliquet in augue id feugiat. Vestibulum vel orci metus. Nam id ullamcorper est. Fusce ligula felis, laoreet at varius vitae, fermentum ut elit. Donec et imperdiet ex.</p><p>" +
                    "Aenean imperdiet, eros et auctor porttitor, orci magna placerat arcu, et dignissim ipsum nisi vitae velit. Nam suscipit eu eros non sagittis. Nam purus magna, faucibus quis elementum sit amet, rhoncus a leo. Nulla a vehicula sem, sed maximus magna. Vestibulum commodo mauris vel ullamcorper elementum. Nam et arcu varius, molestie nunc ac, ultrices neque. Ut aliquam ligula ac nulla maximus condimentum. Donec elementum placerat malesuada. Aenean purus lacus, luctus id neque nec, bibendum pretium arcu. Etiam porttitor nec diam ut ornare. In hendrerit felis in risus cursus, id volutpat nunc gravida.</p><p>" +
                    "Vestibulum vel ante mattis, elementum risus vitae, mattis diam. Sed euismod orci lobortis, pellentesque justo a, bibendum justo. Praesent molestie arcu ligula, vel facilisis dui mollis non. Nam congue non nulla non blandit. Pellentesque in tincidunt quam, sit amet eleifend quam. Aenean gravida convallis nunc vel cursus. Integer hendrerit enim et euismod ultrices. Donec at metus et leo eleifend semper nec at arcu. Suspendisse ex ex, rutrum vitae pulvinar in, rhoncus at libero. Integer ipsum augue, convallis ut lorem non, dictum imperdiet mauris. Duis euismod elit lectus, id fermentum justo facilisis rutrum. Nunc nec placerat augue. Suspendisse vitae massa venenatis, pulvinar leo non, dictum tellus. Donec vel quam condimentum, luctus massa eget, pellentesque nisi.</p><p>" +
                    "Fusce tortor est, ultricies quis nisi et, tempor sodales neque. Etiam commodo pretium condimentum. Pellentesque dapibus quis sem sit amet tristique. In hac habitasse platea dictumst. Ut sapien massa, tempus a lacus ut, tincidunt tincidunt urna. Maecenas laoreet orci sed mi vestibulum congue. Nulla et leo sit amet erat condimentum condimentum. Nam at hendrerit purus, vestibulum vulputate nulla. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla in diam nec odio auctor ornare vel eu ante. In hac habitasse platea dictumst. Proin blandit non tellus eu ultrices.</p><p>" +
                    "Mauris eget venenatis ante. Integer malesuada massa nunc, vel tristique eros lobortis ut. Pellentesque sed ante sed quam efficitur semper nec eget massa. Praesent nec nisl quis neque facilisis dignissim eu eu justo. Praesent malesuada orci non diam consequat, at iaculis enim fermentum. Maecenas iaculis risus at vestibulum lobortis. Aliquam mattis facilisis aliquet. Praesent tempor, nulla tempus aliquet laoreet, tellus sem eleifend diam, vel ultrices dui justo at ligula. Proin et eros sit amet erat molestie efficitur. Sed ut libero ac mauris auctor euismod. In at elit quis turpis congue tempus et vitae diam. Aliquam ullamcorper enim et viverra tristique.</p><p>" +
                    "Pellentesque interdum diam id enim venenatis consectetur. Donec lacinia a urna a pretium. Cras eget elementum ipsum, et auctor orci. Cras diam diam, dapibus a tellus ut, eleifend molestie justo. Nullam ultrices molestie maximus. Suspendisse orci tortor, consequat eget viverra non, interdum ultricies sapien. Maecenas vulputate aliquam dolor quis dapibus. Pellentesque tempus vitae tortor sed gravida. Aliquam pulvinar accumsan quam. Phasellus nec neque eleifend, hendrerit metus non, volutpat eros. Nullam aliquam et elit vel vehicula. Pellentesque dapibus mi at ligula mollis luctus. Ut in dolor interdum, lobortis urna vitae, rutrum lacus. Mauris sodales tellus nibh, ac dictum massa viverra sed. Vestibulum cursus posuere augue, sit amet volutpat eros pulvinar vitae.</p><p>" +
                    "Fusce imperdiet neque id tempus maximus. Pellentesque in ligula turpis. Praesent pretium quam tellus, id cursus quam dignissim in. Nullam eu quam orci. Vestibulum neque metus, fermentum eu gravida at, ornare scelerisque ex. Aliquam erat volutpat. Vestibulum nec pharetra diam. Integer ac risus eleifend, iaculis velit nec, tincidunt justo. Mauris sit amet lorem lorem. Cras facilisis a ex non consectetur. Sed vitae sagittis leo. Donec eu velit feugiat, blandit quam sed, ultrices nisi. Sed nec lobortis enim. Vestibulum in nunc quis enim porttitor vehicula.</p><p>" +
                    "Maecenas eget nisi eros. Nullam eget sagittis nisi. Vivamus et rhoncus risus. Nam sed felis eu massa dignissim dapibus. Sed et lacinia augue. Cras elementum eleifend nibh et scelerisque. Donec efficitur libero augue, quis ullamcorper purus condimentum non.</p><p>" +
                    "Sed ut dignissim odio. Vestibulum sed posuere massa, at fermentum ipsum. Nunc elit est, venenatis imperdiet semper nec, fringilla viverra velit. Quisque ultricies condimentum tempor. Quisque in ipsum pharetra, sollicitudin ex vel, vehicula dolor. Aenean pharetra pharetra nulla nec vehicula. Quisque ut enim ipsum. Curabitur nulla risus, euismod id nisi vel, fringilla pellentesque velit. Maecenas id dignissim libero, ut eleifend neque. Curabitur et mauris et arcu fringilla malesuada. Mauris nec venenatis dui. Maecenas dictum tortor a finibus interdum. Curabitur consectetur condimentum augue quis semper. Phasellus pellentesque pulvinar elit non semper.</p><p>" +
                    "Praesent luctus, purus et commodo dignissim, lorem lorem commodo erat, a dapibus velit mauris et ipsum. Sed at risus at urna pulvinar placerat ut et eros. Nunc varius lacinia elit eu fringilla. Nulla nec erat vestibulum mauris ullamcorper tristique. Etiam vitae eleifend nulla. Nunc faucibus sit amet libero vel porta. Suspendisse lorem nulla, varius id mi a, aliquet ultrices mauris. Donec semper felis eu quam pulvinar, et volutpat libero congue. Nam sed suscipit est, eu facilisis nibh. Vivamus leo urna, consequat vitae auctor ut, pharetra a magna. Phasellus nec euismod urna. Donec gravida, est ac aliquet gravida, risus nisl vehicula felis, dignissim hendrerit lectus arcu pharetra purus. Sed faucibus vehicula ex et imperdiet. Suspendisse potenti.</p>")
    else description

    fun getProgress() = if (position != -1F) position else {
        val lastTime = time
        var max = 0F
        var progress = 0F
        if (lastTime > 0) {
            max = length / 1000F
            progress = lastTime / 1000F
        }
        if (lastTime == -1L) 0F else (progress / max).coerceAtMost(1F).coerceAtLeast(0F)
    }
}