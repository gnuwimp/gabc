# toMP3
toMP3 is an open source audio converter written in [kotlin](https://kotlinlang.org).<br>
toMP3 converts an directory with audio or video files into one single mp3 file.<br>
It uses lame for encoding and decoding mp3.<br>
And ffmpeg for decoding aac/flac/ogg/wav audio files and avi/mkv/mp4 video files.<br>
All input files must have same samplerate and number of channels (stereo/mono) and bitwidth.<br>
Beware that all files that are lossy encoded will be lose some audio quality when transcoded.<br>
toMP3 is released under the [GNU General Public License v3.0](LICENSE).

## Download
Download toMP3 [here](https://github.com/gnuwimp/toMP3/releases).

You will need [Java](https://java.com) installed to run this program.<br>
And [lame](https://lame.sourceforge.io) and optional [ffmpeg](https://www.ffmpeg.org).<br>
It should run on all operating systems that has Java and lame installed.<br>
Lame and ffmpeg must be in application path.<br>

## Run
Double click toMP3.jar file on windows to start the program.<br>
Or run it from the command line with <code>java -jar toMP3.jar</code>.<br>
toMP3 has been tested on Windows 10 and Ubuntu 21.04.<br>

<b>Command Line Arguments</b>.<br>
It can also use arguments, (use only ascii characters on Windows).<br>
And use "" around text and paths with spaces.<br>
<pre>
--src  [source]            source directory with audio files
--dest [destination]       destination directory for target file
--cover [filename]         track cover image (optional)
--artist [name]            artist name
--title [name]             album and title name
--comment [comment tag]    comment string (optional)
--year [recording year]    track year (optional, 1 - 2100)
--genre [genre]            genre string (default Audiobook, optional)
--bitrate [mp3 bitrate]    bitrate for target file (32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, optional, default 48)
--gap [SECONDS]            insert silence between tracks (1 - 5 seconds, optional)
--mono                     convert stereo to mono (optional)
--vbr                      use VBR mode (optional)
--auto                     start automatically and quit after successful encoding
--auto2                    start automatically and quit even for error
</pre>

## Screenshots
<img src="images/tomp3.png" width="50%" height="50%"/>

## Changes
<pre>
2.2:    renamed from gabc to toMP3
        added support for more input files (aac/flac/ogg/wav/avi/mkv/mp4)
        options for mono/stereo and vbr
        insert silence between tracks
        bug fixes

2.1:    bug fixes
</pre>
