# gABC
gABC is an open source mp3 audio book converter written in [kotlin](https://kotlinlang.org).<br />
It is an frontend for lame encoder/decoder.<br />
gABC takes a directory or an CD of mp3 files as input and converts them into one single mp3 file.<br />
The bitrate can be changed during transcoding.<br />
48 kbps is usually sufficient for speech in mono.<br />
All mp3 files must have same samplerate and number of channels (stereo/mono).<br />
The result file will be in mono<br />
Be aware that the files will be transcoded and sound quality will be degraded somewhat.<br />

You will need [Java](http://java.com) installed to run this program.<br />
The actual transcoding of audio files are done by [lame](http://lame.sourceforge.net).<br />
And lame must be in application path.<br />
Download lame for windows at [rarewares](https://www.rarewares.org/mp3-lame-bundle.php).<br />

<hr>

## License & Download
gABC is released under the [GNU General Public License v3.0](LICENSE).<br />
Downloaded from [here](https://github.com/gnuwimp/gabc/releases)<br />
Double click to start the program or run it from the command line with "<code>java -jar gabc.jar</code>".<br />

<hr>

## Screenshots
<img src="images/gabc.png" width="50%" height="50%"/>
