object model {
  opaque type User = String
  object User {
    def apply(name: String): User = name
  }

  opaque type Artist = String
  object Artist {
    def apply(name: String): Artist = name
  }

  case class Song(artist: Artist, title: String)

  enum MusicGenre {
    case House
    case Funk
    case HipHop
  }

  enum PlaylistKind {
    case CuratedByUser(user: User)
    case BasedOnArtist(artist: Artist)
    case BasedOnGenres(genres: Set[MusicGenre])
  }

  case class Playlist(name: String, kind: PlaylistKind, songs: List[Song])
}
import model._, model.MusicGenre._, model.PlaylistKind._

object ch07_Playlist extends App {
  val fooFighters = Artist("Foo Fighters")
  val playlist1   = Playlist(
    "This is Foo Fighters",
    BasedOnArtist(fooFighters),
    List(Song(fooFighters, "Breakout"), Song(fooFighters, "Learn To Fly"))
  )

  val playlist2 = Playlist(
    "Deep Focus",
    BasedOnGenres(Set(House, Funk)),
    List(Song(Artist("Daft Punk"), "One More Time"), Song(Artist("The Chemical Brothers"), "Hey Boy Hey Girl"))
  )

  val playlist3 = Playlist(
    "My Playlist",
    CuratedByUser(User("Michał Płachta")),
    List(Song(fooFighters, "My Hero"), Song(Artist("Iron Maiden"), "The Trooper"))
  )

  /**
    * 这一章节的精华在于List[Playlist]=>List[Song]不一定用map或者filter实现，foldLeft同样可以，而且可能更好
    * @param playlists
    * @param artist
    * @param genre
    * @return
    */
  def gatherSongs(playlists: List[Playlist], artist: Artist, genre: MusicGenre): List[Song] =
    playlists.foldLeft(List.empty[Song])((songs, playlist) =>
      val matchingSongs = playlist.kind match {
        case CuratedByUser(user)           => playlist.songs.filter(_.artist == artist)
        case BasedOnArtist(playlistArtist) => if (playlistArtist == artist) playlist.songs else List.empty
        case BasedOnGenres(genres)         => if (genres.contains(genre)) playlist.songs else List.empty
      }
      songs.appendedAll(matchingSongs)
    )

  /**
    * 用flatMap实现也可以
    * @param playlists
    * @param artist
    * @param genre
    * @return
    */
  def gatherSongsWithMap(playlists: List[Playlist], artist: Artist, genre: MusicGenre): List[Song] = {
    playlists.flatMap { playlist =>
      playlist.kind match {
        case CuratedByUser(user) => playlist.songs.filter(_.artist == artist)
        case BasedOnArtist(playlistArtist) => if (playlistArtist == artist) playlist.songs else List.empty
        case BasedOnGenres(genres) => if (genres.contains(genre)) playlist.songs else List.empty
      }
    }
  }


  assert(
    gatherSongs(List(playlist1, playlist2, playlist3), fooFighters, Funk)
      == playlist1.songs.appendedAll(playlist2.songs).appended(Song(fooFighters, "My Hero"))
  )

  assert(
    gatherSongsWithMap(List(playlist1, playlist2, playlist3), fooFighters, Funk)
      == playlist1.songs.appendedAll(playlist2.songs).appended(Song(fooFighters, "My Hero"))
  )
}
