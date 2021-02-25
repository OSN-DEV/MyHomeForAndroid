package jp.gr.java_conf.osndev.myhome.weather

import android.util.Log
import jp.gr.java_conf.osndev.myhome.IServiceReceiver
import org.jsoup.Jsoup

class WeatherDataRepo {
    // https://qiita.com/opengl-8080/items/d4864bbc335d1e99a2d7
    // https://weather.yahoo.co.jp/weather/jp/1b/1400.html
    companion object {
        var TAG = WeatherDataRepo::class.simpleName ?: ""
    }

    fun getCurrent(callback: IServiceReceiver) {
        Log.d(TAG, "Start to read Weather page")
        val document = Jsoup.connect("https://weather.yahoo.co.jp/weather/jp/1b/1400.html").get()

        try {
            val root = document.select("div.forecastCity")[0]
            val weather = eraseTag(
                root.select(".pict")[0].html() +
                        "(" + root.select(".high")[0].html() + "/" +
                        root.select(".low")[0].html() + ")"
            )

            val precips = root.select(".precip td")
            val p1 = precips[0].html()
            val p2 = precips[1].html()
            val p3 = precips[2].html()
            val p4 = precips[3].html()

            callback.onWeatherData(WeatherData(weather, p1, p2, p3, p4))
        } catch (ex: Exception) {
            Log.e(TAG, ex.message ?: "")
        }
    }

    private fun eraseTag(data: String): String {
        return data.replace("<.+?>".toRegex(), "")
            .replace("\\[.+?]".toRegex(), "")
    }
}