package kz.nurtaza.besikjyry

import android.app.Activity
import android.app.AlertDialog
import android.Manifest
import android.content.pm.PackageManager
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Environment
import java.io.File
import android.content.Intent
import android.content.Context
import android.location.LocationManager
import android.graphics.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.view.*
import android.widget.Toast
import kotlin.random.Random
import kotlin.math.*

data class Constellation(val kz: String, val en: String, val ru: String, val points: List<Pair<Float,Float>>, val edges: List<Pair<Int,Int>>)
private fun shape(kz:String, en:String, ru:String, coords:String, links:String):Constellation {
    val p=coords.split(" ").map { val a=it.split(","); Pair(a[0].toFloat(),a[1].toFloat()) }
    val e=links.split(" ").map { val a=it.split("-"); Pair(a[0].toInt(),a[1].toInt()) }
    return Constellation(kz,en,ru,p,e)
}
// Constellation stick figures derived from d3-celestial constellation line data (ofrohn/d3-celestial).
// Coordinates are normalized from sky coordinates for a decorative local view; not a live sky map.
private val catalog = listOf(
    shape("Жетіқарақшы","Ursa Major","Большая Медведица","0.6510,0.3579 0.4883,0.2890 0.4840,0.3674 0.6020,0.4066 0.7386,0.3735 0.8064,0.3886 0.8600,0.4706 0.5843,0.4929 0.5217,0.7073 0.5211,0.7301 0.5017,0.5408 0.3943,0.5846 0.3824,0.5640 0.2790,0.2699 0.1400,0.3041 0.3232,0.3286 0.3257,0.4012 0.2820,0.4361 0.2057,0.4891 0.2157,0.5020","0-1 1-2 2-3 3-0 0-4 4-5 5-6 3-7 7-8 8-9 7-10 10-11 10-12 1-13 13-14 14-15 15-2 2-16 16-17 17-18 19-17"),
    shape("Кіші аю","Ursa Minor","Малая Медведица","0.1948,0.5566 0.2291,0.5985 0.1708,0.6790 0.1400,0.6313 0.2584,0.4694 0.3059,0.3760 0.8600,0.3210","0-1 1-2 2-3 3-0 0-4 4-5 5-6"),
    shape("Аңшы","Orion","Орион","0.7191,0.2724 0.6403,0.1400 0.6973,0.1433 0.7452,0.2859 0.6881,0.3956 0.6450,0.4494 0.4656,0.4748 0.2850,0.3834 0.3068,0.5863 0.2812,0.5688 0.2630,0.4927 0.2548,0.4601 0.2594,0.4135 0.2938,0.3026 0.3428,0.2571 0.3734,0.2525 0.4023,0.8247 0.4617,0.6851 0.5067,0.6347 0.5254,0.3887 0.5589,0.6742 0.6007,0.8600 0.5318,0.6564","0-1 1-2 2-3 3-4 4-5 5-6 6-7 8-9 9-10 10-11 11-12 12-7 7-13 13-14 14-15 16-17 17-18 18-6 6-19 19-5 5-20 20-21 20-22 22-18"),
    shape("Кассиопея","Cassiopeia","Кассиопея","0.8600,0.3044 0.6644,0.4928 0.4653,0.4664 0.3544,0.6956 0.1400,0.5523","0-1 1-2 2-3 3-4"),
    shape("Аққу","Cygnus","Лебедь","0.8128,0.7958 0.6685,0.6897 0.5389,0.5115 0.3377,0.3734 0.2553,0.1864 0.1872,0.1400 0.6426,0.3692 0.3989,0.6581 0.2608,0.8600","0-1 1-2 2-3 3-4 4-5 6-2 2-7 7-8"),
    shape("Лира","Lyra","Лира","0.4340,0.3488 0.4258,0.1400 0.2709,0.2262 0.6367,0.4223 0.7291,0.8600 0.5445,0.7900","0-1 1-2 2-0 0-3 3-4 4-5 5-0"),
    shape("Сарышаян","Scorpius","Скорпион","0.1481,0.3338 0.1577,0.2265 0.1907,0.1400 0.2927,0.3178 0.3459,0.3436 0.3878,0.3984 0.4802,0.5851 0.4913,0.7005 0.5088,0.8330 0.6226,0.8600 0.7855,0.8526 0.8519,0.7644 0.8189,0.7307 0.7614,0.6715","0-1 1-2 1-3 3-4 4-5 5-6 6-7 7-8 8-9 9-10 10-11 11-12 12-13"),
    shape("Арыстан","Leo","Лев","0.2716,0.6731 0.2655,0.5548 0.3394,0.4789 0.6558,0.4621 0.8600,0.6089 0.6565,0.5877 0.3202,0.3908 0.1804,0.3269 0.1400,0.3820","0-1 1-2 2-3 3-4 4-5 5-0 2-6 6-7 7-8"),
    shape("Торпақ","Taurus","Телец","0.8600,0.3516 0.5254,0.4552 0.4861,0.4695 0.4380,0.4749 0.4550,0.4321 0.4858,0.3955 0.7985,0.1847 0.3344,0.5450 0.1528,0.6067 0.3478,0.6904 0.1400,0.6224 0.2054,0.8153","0-1 1-2 2-3 3-4 4-5 5-6 3-7 7-8 8-9 8-10 10-11"),
    shape("Егіздер","Gemini","Близнецы","0.1400,0.4960 0.2043,0.4958 0.3713,0.4053 0.5879,0.2285 0.7747,0.1717 0.8600,0.3052 0.7852,0.3443 0.6594,0.5142 0.5319,0.5630 0.3218,0.7071 0.3821,0.8283 0.6433,0.7023","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 9-10 7-11"),
    shape("Андромеда","Andromeda","Андромеда","0.8600,0.3731 0.6457,0.5081 0.5254,0.6039 0.4030,0.6395 0.5961,0.7536 0.5571,0.7366 0.5223,0.6350 0.5157,0.5464 0.2833,0.3542 0.1400,0.3732 0.2923,0.3328 0.2810,0.2900 0.5943,0.4502 0.5669,0.3983 0.6448,0.2743 0.7575,0.2464 0.3145,0.2908","0-1 1-2 2-3 4-5 5-6 6-2 2-7 7-8 8-9 8-10 10-11 1-12 12-13 13-14 14-15 10-16"),
    shape("Пегас","Pegasus","Пегас","0.2646,0.2212 0.4241,0.2823 0.5245,0.3265 0.8366,0.3057 0.8600,0.5931 0.5292,0.5926 0.4419,0.6553 0.4167,0.6830 0.2657,0.7788 0.1400,0.7028 0.4579,0.3985 0.4412,0.4199 0.2503,0.3831 0.1422,0.3769","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 5-2 2-10 10-11 11-12 12-13"),
    shape("Персей","Perseus","Персей","0.7144,0.8124 0.7611,0.8233 0.7841,0.7181 0.7789,0.6045 0.7186,0.5353 0.7078,0.3950 0.6771,0.3841 0.6192,0.3392 0.5263,0.2410 0.4591,0.1767 0.4761,0.2611 0.5466,0.3459 0.5486,0.4739 0.5423,0.5790 0.5572,0.6152 0.5281,0.6360 0.4975,0.6138 0.5018,0.5769 0.8204,0.3260 0.8600,0.3783 0.8303,0.3971 0.4282,0.3562 0.1400,0.3169","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 9-10 10-11 11-12 12-13 13-14 14-15 15-16 16-17 17-13 18-19 19-20 20-5 11-21 21-22"),
    shape("Айдаһар","Draco","Дракон","0.6938,0.5679 0.6983,0.6377 0.6604,0.6272 0.6630,0.5900 0.8084,0.4280 0.7333,0.3804 0.6290,0.4533 0.5641,0.5077 0.5320,0.5460 0.4785,0.5408 0.3617,0.4706 0.2300,0.4004 0.1400,0.4064 0.7337,0.3623 0.8600,0.3942","0-1 1-2 2-3 3-0 0-4 4-5 5-6 6-7 7-8 8-9 9-10 10-11 11-12 5-13 4-14"),
    shape("Бүркіт","Aquila","Орёл","0.5756,0.2648 0.6189,0.3319 0.6622,0.4264 0.8151,0.7040 0.6350,0.6338 0.3770,0.5528 0.1849,0.1400 0.1929,0.8600","0-1 1-2 2-3 3-4 4-5 5-6 6-1 1-5 5-7"),
    shape("Мерген","Sagittarius","Стрелец","0.2474,0.6656 0.2841,0.6055 0.2663,0.4904 0.3053,0.3791 0.2258,0.2689 0.6111,0.8600 0.6181,0.7629 0.4991,0.4917 0.4042,0.4188 0.7936,0.7946 0.8187,0.6280 0.7969,0.4013 0.6898,0.3655 0.6259,0.3561 0.5714,0.3750 0.4580,0.4012 0.1813,0.5055 0.5233,0.4359 0.5107,0.2862 0.5391,0.2680 0.5831,0.2157 0.6057,0.1878 0.6060,0.1400 0.4718,0.2701 0.4519,0.3115","0-1 1-2 2-3 3-4 5-6 6-7 7-8 8-3 9-10 10-11 11-12 12-13 13-14 14-15 15-8 8-2 2-16 16-1 1-7 7-17 17-15 15-18 18-19 19-20 20-21 21-22 18-23 23-24 24-15"),
    shape("Үлкен төбет","Canis Major","Большой Пёс","0.2265,0.3764 0.4346,0.3268 0.6003,0.6111 0.6500,0.7134 0.5882,0.7750 0.5595,0.8164 0.2044,0.8600 0.7956,0.8296 0.5365,0.3403 0.6071,0.2836 0.5184,0.1400","0-1 1-2 2-3 3-4 4-5 5-6 7-3 1-8 8-9 9-10 10-8"),
    shape("Бақташы","Boötes","Волопас","0.3248,0.7896 0.3543,0.7718 0.4376,0.7570 0.5018,0.5457 0.5028,0.3958 0.6213,0.3564 0.6752,0.4901 0.5540,0.6079 0.5388,0.8600 0.4405,0.2488 0.4289,0.1412 0.4754,0.1400","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-2 2-8 4-9 9-10 10-11 11-9"),
    shape("Солтүстік тәж","Corona Borealis","Северная Корона","0.2493,0.2432 0.1400,0.4620 0.2869,0.6941 0.4595,0.7347 0.6062,0.7568 0.7774,0.6782 0.8600,0.3896","0-1 1-2 2-3 3-4 4-5 5-6"),
    shape("Оңтүстік крест","Crux","Южный Крест","0.7474,0.4498 0.2526,0.3367 0.4266,0.8600 0.4959,0.1400","0-1 2-3"),
    shape("Сорғы","Antlia","Насос","0.1400,0.5740 0.6166,0.3784 0.8600,0.6216","0-1 1-2"),
    shape("Жұмақ құсы","Apus","Райская Птица","0.1400,0.5954 0.7180,0.5518 0.8600,0.4046 0.7998,0.5770","0-1 1-2 2-3"),
    shape("Суқұйғыш","Aquarius","Водолей","0.1400,0.4933 0.1606,0.4848 0.3215,0.4279 0.4630,0.3403 0.5287,0.3581 0.5584,0.3353 0.5853,0.3369 0.6567,0.4613 0.7613,0.4881 0.7263,0.6880 0.4657,0.5662 0.5087,0.4647 0.5437,0.3120 0.7823,0.6701 0.8600,0.6321","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 2-10 3-11 5-12 13-8 8-14"),
    shape("Құрбандық орны","Ara","Жертвенник","0.6365,0.5731 0.6899,0.8600 0.3032,0.7506 0.3859,0.5473 0.3949,0.3588 0.6968,0.1400 0.6356,0.5167","0-1 1-2 2-3 3-4 4-5 5-6"),
    shape("Тоқты","Aries","Овен","0.8600,0.2797 0.3140,0.4898 0.1542,0.6366 0.1400,0.7203","0-1 1-2 2-3"),
    shape("Арбакеш","Auriga","Возничий","0.6626,0.4018 0.4392,0.3724 0.3861,0.5059 0.3364,0.7322 0.4892,0.8600 0.6636,0.6187 0.6625,0.1400 0.3624,0.4333 0.3650,0.5104","0-1 1-2 2-3 3-4 4-5 5-0 0-6 6-1 1-7 7-8"),
    shape("Қашау","Caelum","Резец","0.2551,0.8600 0.3970,0.6251 0.4188,0.2663 0.7449,0.1400","0-1 1-2 2-3"),
    shape("Керік","Camelopardalis","Жираф","0.4446,0.8600 0.4642,0.6526 0.4342,0.4697 0.2299,0.3150 0.2272,0.4950 0.1616,0.6682 0.7062,0.3774 0.8384,0.1400","0-1 1-2 2-3 3-4 4-5 2-6 6-7"),
    shape("Шаян","Cancer","Рак","0.6836,0.7617 0.5628,0.5302 0.5506,0.4083 0.5802,0.1400 0.3164,0.8600","0-1 1-2 2-3 1-4"),
    shape("Аңшы иттер","Canes Venatici","Гончие Псы","0.8600,0.7563 0.1400,0.2437","0-1"),
    shape("Кіші төбет","Canis Minor","Малый Пёс","0.8544,0.8600 0.1456,0.1400","0-1"),
    shape("Тауешкі","Capricornus","Козерог","0.1400,0.2558 0.1671,0.3328 0.2303,0.4356 0.3691,0.6884 0.4152,0.7442 0.6959,0.5914 0.8600,0.3784 0.8040,0.3966 0.6603,0.4024 0.5290,0.4159","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 9-0"),
    shape("Кеме қыры","Carina","Киль","0.1744,0.2382 0.1400,0.4235 0.5622,0.7555 0.7132,0.7618 0.7861,0.6517 0.7588,0.5989 0.7215,0.5920 0.5719,0.5519 0.4357,0.5564 0.3715,0.4291 0.4034,0.3190 0.4911,0.4628 0.8449,0.6133 0.8499,0.6040 0.8600,0.5722 0.8500,0.5460 0.8123,0.5436","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 9-10 10-11 11-7 4-12 12-13 13-14 14-15 15-16 16-5"),
    shape("Кентавр","Centaurus","Центавр","0.1400,0.6177 0.2963,0.5424 0.3612,0.5325 0.4057,0.5071 0.5984,0.5972 0.6500,0.4737 0.6305,0.3774 0.6301,0.3617 0.6868,0.2554 0.7819,0.3711 0.8600,0.3700 0.5347,0.2622 0.7954,0.7446 0.6774,0.7354 0.3071,0.5753 0.1755,0.7167","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 9-10 7-11 12-4 4-13 2-14 14-15"),
    shape("Цефей","Cepheus","Цефей","0.1400,0.6511 0.1996,0.6912 0.3259,0.6653 0.4205,0.7976 0.5401,0.8580 0.5242,0.8177 0.5937,0.8103 0.6716,0.5396 0.8600,0.1420 0.3642,0.3879","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 9-2 9-7"),
    shape("Кит","Cetus","Кит","0.7761,0.3730 0.7433,0.3313 0.7091,0.2806 0.7833,0.2513 0.8487,0.2726 0.8600,0.3579 0.7592,0.4245 0.6702,0.4830 0.5469,0.6133 0.5142,0.7125 0.2468,0.7487 0.1400,0.5865 0.3574,0.6106 0.4256,0.5752","0-1 1-2 2-3 3-4 4-5 5-0 0-6 6-7 7-8 8-9 9-10 10-11 11-12 12-13 13-8"),
    shape("Құбылғы","Chamaeleon","Хамелеон","0.1400,0.3934 0.5511,0.4948 0.5806,0.6066 0.8600,0.5371 0.8038,0.4716","0-1 1-2 2-3 3-4 4-1"),
    shape("Циркуль","Circinus","Циркуль","0.7058,0.1400 0.2114,0.8600 0.7886,0.2006","0-1 1-2"),
    shape("Көгершін","Columba","Голубь","0.8600,0.1711 0.4193,0.3347 0.2593,0.2158 0.1400,0.3138 0.5351,0.8289","0-1 1-2 2-3 1-4"),
    shape("Вероника шашы","Coma Berenices","Волосы Вероники","0.8138,0.8600 0.8425,0.1662 0.1575,0.1400","0-1 1-2"),
    shape("Оңтүстік тәж","Corona Australis","Южная Корона","0.6371,0.1768 0.7888,0.1723 0.8490,0.2588 0.8600,0.4066 0.8269,0.5255 0.7237,0.6900 0.4570,0.8277 0.1400,0.7123","0-1 1-2 2-3 3-4 4-5 5-6 6-7"),
    shape("Қарға","Corvus","Ворон","0.2347,0.8600 0.2697,0.6751 0.3857,0.2300 0.6729,0.1400 0.7653,0.7432","0-1 1-2 2-3 3-4 4-1"),
    shape("Тостаған","Crater","Чаша","0.6125,0.1523 0.4579,0.2088 0.3905,0.4180 0.1400,0.6060 0.2921,0.8477 0.4420,0.6317 0.4614,0.5732 0.7159,0.6087 0.8600,0.5447","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 2-6"),
    shape("Дельфин","Delphinus","Дельфин","0.2573,0.8600 0.4140,0.3684 0.4894,0.1717 0.7427,0.1400 0.6275,0.2967","0-1 1-2 2-3 3-4 4-1"),
    shape("Алтын балық","Dorado","Золотая Рыба","0.1819,0.1400 0.2985,0.3198 0.6853,0.6960 0.7576,0.8600 0.8181,0.7263 0.5029,0.4425","0-1 1-2 2-3 3-4 4-2 2-5 5-1"),
    shape("Кіші ат","Equuleus","Малый Конь","0.5999,0.8600 0.5509,0.1584 0.4001,0.1400","0-1 1-2"),
    shape("Эридан","Eridanus","Эридан","0.8150,0.1644 0.7480,0.1400 0.7205,0.1413 0.6471,0.1878 0.6057,0.2768 0.5700,0.2580 0.5614,0.2268 0.5304,0.2227 0.4210,0.2153 0.3841,0.2814 0.3870,0.3443 0.4389,0.4117 0.4902,0.3868 0.5330,0.3851 0.5721,0.4067 0.7181,0.5042 0.6836,0.5503 0.6652,0.5474 0.5800,0.5794 0.5429,0.6338 0.4914,0.6710 0.4265,0.6342 0.3737,0.6282 0.3327,0.7329 0.3013,0.7836 0.2397,0.7849 0.1850,0.8600","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 9-10 10-11 11-12 12-13 13-14 14-15 15-16 16-17 17-18 18-19 19-20 20-21 21-22 22-23 23-24 24-25 25-26"),
    shape("Пеш","Fornax","Печь","0.8600,0.4157 0.6151,0.5843 0.1400,0.4310","0-1 1-2"),
    shape("Тырна","Grus","Журавль","0.7767,0.8600 0.6748,0.7928 0.6262,0.5854 0.5194,0.4387 0.3415,0.5890 0.5154,0.4268 0.4025,0.3263 0.3240,0.2419 0.2233,0.1400","0-1 1-2 2-3 3-4 4-2 5-6 6-7 7-8"),
    shape("Геркулес","Hercules","Геркулес","0.3183,0.7526 0.3578,0.6999 0.4104,0.4718 0.4181,0.3067 0.3763,0.2274 0.3079,0.1400 0.2558,0.1711 0.1792,0.2271 0.5009,0.4870 0.5711,0.3544 0.7671,0.3444 0.6122,0.3468 0.5710,0.6243 0.7205,0.5593 0.7743,0.5249 0.8208,0.5358 0.5692,0.8600","0-1 1-2 2-3 3-4 4-5 5-6 6-7 2-8 3-9 10-11 11-9 9-8 8-12 12-13 13-14 14-15 16-1"),
    shape("Сағат","Horologium","Часы","0.7347,0.1400 0.2904,0.4212 0.2653,0.4788 0.2812,0.5452 0.3927,0.7167 0.3693,0.8600","0-1 1-2 2-3 3-4 4-5"),
    shape("Гидра","Hydra","Гидра","0.1576,0.3421 0.1608,0.3467 0.1508,0.3658 0.1421,0.3662 0.1400,0.3477 0.1743,0.3458 0.2109,0.3743 0.2602,0.4013 0.2365,0.4602 0.2826,0.5086 0.3196,0.4891 0.3495,0.5242 0.3950,0.5192 0.4788,0.6418 0.5173,0.6579 0.6835,0.5738 0.7751,0.6013 0.8600,0.6113","0-1 1-2 2-3 3-4 4-0 0-5 5-6 6-7 7-8 8-9 9-10 10-11 11-12 12-13 13-14 14-15 15-16 16-17"),
    shape("Оңтүстік су жыланы","Hydrus","Южная Гидра","0.1400,0.8217 0.8600,0.6980 0.6183,0.4530 0.5545,0.4691 0.4587,0.4276 0.4724,0.1783","0-1 1-2 2-3 3-4 4-5"),
    shape("Үндіс","Indus","Индеец","0.1400,0.1734 0.1980,0.4443 0.2945,0.8266 0.8600,0.6241 0.5190,0.5337","0-1 1-2 2-3 3-4 4-0"),
    shape("Кесіртке","Lacerta","Ящерица","0.4687,0.1400 0.5352,0.2368 0.5201,0.3649 0.4469,0.4230 0.5283,0.5928 0.6146,0.5354 0.4769,0.2769 0.3854,0.7622 0.4034,0.8600","0-1 1-2 2-3 3-4 4-5 5-2 2-6 6-0 4-7 7-8"),
    shape("Кіші арыстан","Leo Minor","Малый Лев","0.4423,0.5003 0.6106,0.5649 0.8600,0.5462 0.6285,0.4351 0.1400,0.4489","0-1 1-2 2-3 3-0 0-4"),
    shape("Қоян","Lepus","Заяц","0.8600,0.3629 0.7443,0.3248 0.6322,0.3573 0.4635,0.5063 0.2286,0.4260 0.1400,0.7323 0.4103,0.6522 0.6027,0.7362 0.6840,0.6582 0.2322,0.2638 0.3074,0.2755","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 9-4 4-10"),
    shape("Таразы","Libra","Весы","0.4109,0.7013 0.3010,0.3751 0.5187,0.1400 0.6729,0.3309 0.6854,0.8020 0.6990,0.8600","0-1 1-2 2-3 3-4 4-5 1-3"),
    shape("Қасқыр","Lupus","Волк","0.6954,0.1400 0.6135,0.1706 0.4820,0.2427 0.4789,0.4136 0.3117,0.5106 0.1901,0.6764 0.4123,0.8600 0.4581,0.6954 0.4884,0.5712 0.5796,0.4339 0.7625,0.3259 0.8099,0.2638","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 9-10 10-11 3-9"),
    shape("Сілеусін","Lynx","Рысь","0.1400,0.2181 0.2894,0.2316 0.4062,0.4425 0.6290,0.5805 0.7790,0.6126 0.8512,0.7267 0.8600,0.7819","0-1 1-2 2-3 3-4 4-5 5-6"),
    shape("Үстел тауы","Mensa","Столовая Гора","0.8600,0.6315 0.4920,0.8572 0.1400,0.6577 0.2122,0.1428","0-1 1-2 2-3"),
    shape("Микроскоп","Microscopium","Микроскоп","0.3197,0.2379 0.3014,0.8600 0.6986,0.6663 0.6638,0.1400 0.4590,0.1452","0-1 1-2 2-3 3-4 4-0"),
    shape("Бірмүйіз","Monoceros","Единорог","0.6869,0.7462 0.8600,0.5799 0.5009,0.5168 0.2284,0.6825 0.1400,0.6633 0.3489,0.4433 0.1964,0.3881 0.2542,0.3187 0.3054,0.2538","0-1 1-2 2-3 3-4 2-5 5-6 6-7 7-8"),
    shape("Шыбын","Musca","Муха","0.1400,0.2135 0.4402,0.3441 0.6244,0.4687 0.7098,0.3597 0.8600,0.7246 0.5801,0.7865","0-1 1-2 2-3 3-4 4-5 5-2"),
    shape("Бұрыштық","Norma","Наугольник","0.2873,0.1400 0.7928,0.4842 0.6134,0.8600 0.2072,0.7262","0-1 1-2 2-3 3-0"),
    shape("Октант","Octans","Октант","0.1400,0.5906 0.8600,0.5246 0.7668,0.4094","0-1 1-2 2-0"),
    shape("Жылан ұстаушы","Ophiuchus","Змееносец","0.7199,0.5190 0.6731,0.3072 0.6546,0.2756 0.6187,0.1400 0.4621,0.1941 0.3497,0.3195 0.2801,0.4158 0.2968,0.4328 0.3759,0.5325 0.5155,0.6200 0.3506,0.6351 0.3333,0.6664 0.3211,0.6932 0.3273,0.7511 0.5644,0.7774 0.5868,0.8600","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9 4-8 8-10 10-11 11-12 12-13 2-9 9-14 14-15"),
    shape("Тауыс","Pavo","Павлин","0.6617,0.2485 0.7247,0.5429 0.6065,0.5423 0.3569,0.4180 0.2623,0.3965 0.2145,0.4641 0.1400,0.4969 0.3269,0.7054 0.5799,0.7515 0.8600,0.5169","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-1 1-9"),
    shape("Феникс","Phoenix","Феникс","0.2884,0.1697 0.6386,0.3950 0.8346,0.2214 0.8600,0.5152 0.6588,0.8303 0.1400,0.3454","0-1 1-2 2-3 3-4 4-1 1-5 5-0"),
    shape("Суретші","Pictor","Живописец","0.7790,0.8600 0.2443,0.4777 0.2210,0.1400","0-1 1-2"),
    shape("Балықтар","Pisces","Рыбы","0.6648,0.3539 0.6564,0.2637 0.6879,0.3100 0.6555,0.4121 0.7365,0.5054 0.7927,0.6068 0.8600,0.7116 0.8257,0.7047 0.7767,0.6670 0.7312,0.6562 0.6648,0.6327 0.6212,0.6276 0.5635,0.6326 0.3640,0.6444 0.2858,0.6647 0.2374,0.6523 0.2065,0.6687 0.1937,0.7031 0.2332,0.7363 0.2942,0.7277 0.3118,0.6998 0.1400,0.6943","0-1 1-2 2-0 0-3 3-4 4-5 5-6 6-7 7-8 8-9 9-10 10-11 11-12 12-13 13-14 14-15 15-16 16-17 17-18 18-19 19-20 20-14 17-21"),
    shape("Оңтүстік балық","Piscis Austrinus","Южная Рыба","0.6917,0.3616 0.8600,0.4809 0.8431,0.6159 0.8092,0.6315 0.6011,0.6070 0.3721,0.6367 0.1400,0.6384 0.1676,0.5400","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-5 5-0"),
    shape("Кеменің арты","Puppis","Корма","0.2105,0.7381 0.4590,0.5586 0.5740,0.3016 0.5958,0.2555 0.6618,0.1983 0.7095,0.1400 0.7769,0.1819 0.7520,0.6441 0.7895,0.8600 0.6542,0.2300 0.6272,0.3188","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 4-9 9-10 10-2"),
    shape("Компас","Pyxis","Компас","0.2152,0.8600 0.6582,0.5850 0.7006,0.4608 0.7848,0.1400","0-1 1-2 2-3"),
    shape("Тор","Reticulum","Сетка","0.7155,0.5548 0.7471,0.1400 0.4756,0.4144 0.2529,0.8600","0-1 1-2 2-3 3-0"),
    shape("Жебе","Sagitta","Стрела","0.1400,0.5765 0.4213,0.4918 0.8600,0.3360 0.1767,0.6640","0-1 1-2 3-1"),
    shape("Мүсінші","Sculptor","Скульптор","0.8600,0.3770 0.3572,0.3352 0.1400,0.4850 0.2421,0.6648","0-1 1-2 2-3"),
    shape("Қалқан","Scutum","Щит","0.4461,0.3964 0.6628,0.1400 0.5740,0.4557 0.3372,0.8600","0-1 1-2 2-3 3-0"),
    shape("Жылан","Serpens","Змея","0.1807,0.3073 0.1641,0.2464 0.1898,0.2683 0.2174,0.3039 0.1400,0.3773 0.1738,0.4362 0.1972,0.4641 0.2814,0.5812 0.4817,0.7536 0.5789,0.7489 0.6556,0.6683 0.6701,0.6455 0.7352,0.5698 0.8600,0.4680","0-1 1-2 2-3 3-0 0-4 4-5 5-6 6-7 8-9 9-10 10-11 11-12 12-13"),
    shape("Секстант","Sextans","Секстант","0.4340,0.2049 0.1400,0.7951 0.8445,0.3856 0.8600,0.2251","0-1 1-2 2-3"),
    shape("Телескоп","Telescopium","Телескоп","0.1533,0.1400 0.7735,0.1433 0.8467,0.8600","0-1 1-2"),
    shape("Үшбұрыш","Triangulum","Треугольник","0.1580,0.8600 0.6227,0.1400 0.8420,0.2918","0-1 1-2 2-0"),
    shape("Оңтүстік үшбұрыш","Triangulum Australe","Южный Треугольник","0.8600,0.7351 0.4307,0.2649 0.1400,0.7058","0-1 1-2 2-0"),
    shape("Тукан","Tucana","Тукан","0.1400,0.4229 0.4589,0.3282 0.8600,0.5492 0.7979,0.6389 0.6888,0.6718 0.1878,0.6432","0-1 1-2 2-3 3-4 4-5 5-0"),
    shape("Желкен","Vela","Паруса","0.3011,0.6922 0.4724,0.7006 0.6315,0.6883 0.8600,0.5464 0.7133,0.3451 0.5117,0.2994 0.4077,0.3812 0.1400,0.4889","0-1 1-2 2-3 3-4 4-5 5-6 6-7"),
    shape("Бикеш","Virgo","Дева","0.1400,0.3941 0.1593,0.4702 0.2759,0.5090 0.3627,0.5215 0.4756,0.5868 0.5365,0.6766 0.7393,0.5942 0.8473,0.5887 0.4446,0.3234 0.4184,0.4441 0.5744,0.5079 0.6820,0.4737 0.8600,0.4682","0-1 1-2 2-3 3-4 4-5 5-6 6-7 8-9 9-3 4-10 10-11 11-12"),
    shape("Ұшқыш балық","Volans","Летучая Рыба","0.8600,0.3711 0.6275,0.3538 0.5148,0.5201 0.1912,0.4758 0.1400,0.6462","0-1 1-2 2-3 3-4 4-2 2-0"),
    shape("Түлкі","Vulpecula","Лисичка","0.1400,0.6716 0.2910,0.4967 0.5903,0.5279 0.6827,0.3317 0.8600,0.3284","0-1 1-2 2-3 3-4")
)

class MainActivity: Activity() {
    private val prefs by lazy { getSharedPreferences("settings", Context.MODE_PRIVATE) }
    private var player: MediaPlayer? = null
    private var tracks = mutableListOf<Uri>()
    private var queue = mutableListOf<Uri>()
    private var history = mutableListOf<Uri>()
    private var current: Uri? = null
    private val random = Random(System.currentTimeMillis())
    private lateinit var sky: SkyView
    private val moonLocationPermission=4017
    private fun showMoonWithLocation() {
        if (android.os.Build.VERSION.SDK_INT>=23 && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),moonLocationPermission)
        } else showMoonNow()
    }
    private fun showMoonNow() {
        var latitude:Double?=null
        var longitude:Double?=null
        try {
            val manager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location=listOf(LocationManager.NETWORK_PROVIDER,LocationManager.GPS_PROVIDER)
                .mapNotNull { provider -> try { manager.getLastKnownLocation(provider) } catch (_:Exception) { null } }
                .maxByOrNull { it.time }
            latitude=location?.latitude
            longitude=location?.longitude
        } catch (_:Exception) { }
        sky.showMoon(latitude,longitude)
    }
    private lateinit var updater: Updater
    private val handler = Handler(Looper.getMainLooper())
    private var startedAt = 0L
    private val sessionMs=60*60*1000L
    private val fadeMs=5*60*1000L
    private val stop = Runnable { player?.stop(); player?.release(); player=null; finish() }
    private val fadeAudio = object:Runnable {
        override fun run() {
            val remaining=(startedAt+sessionMs-SystemClock.elapsedRealtime()).coerceAtLeast(0L)
            val volume=(remaining.toFloat()/fadeMs).coerceIn(0f,1f)
            player?.setVolume(volume,volume)
            if(remaining>0L) handler.postDelayed(this,1000L)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        sky = SkyView(this)
        setContentView(sky)
        updater = Updater(this)
        updater.checkOnLaunch()
        startedAt = SystemClock.elapsedRealtime()
        sky.setSessionEnd(startedAt+sessionMs,fadeMs)
        handler.postDelayed(stop,sessionMs)
        handler.postDelayed(fadeAudio,sessionMs-fadeMs)
        ensureTunMusicFolder()
        ensureMusicAccess()
        /* val stored=prefs.getString("folder",null)
        if(stored==null) promptFolder() else {
            try { loadFolder(Uri.parse(stored)); if(tracks.isEmpty()) promptFolder("MP3 не найдены. Выберите Music/Tun.") else playNext() }
            catch (e:Exception) { promptFolder("Нет доступа к папке: ${e.message}") }
        } */
    }
    private fun ensureTunMusicFolder() {
        val folder=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),"Tun")
        if(folder.isDirectory || folder.mkdirs()) return
        // Scoped storage may block direct mkdirs on newer Android TV devices.
        // A tiny text file created through MediaStore establishes Music/Tun without storage-wide access.
        if(Build.VERSION.SDK_INT>=29) {
            try {
                val values=ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME,"README_Tun.txt")
                    put(MediaStore.MediaColumns.MIME_TYPE,"text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH,"Music/Tun/")
                }
                val uri=contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),values)
                if(uri!=null) contentResolver.openOutputStream(uri)?.use {
                    it.write("Place MP3 files in Music/Tun for Tün.".toByteArray(Charsets.UTF_8))
                }
            } catch(_:Exception) { /* Folder instructions remain visible if storage denies creation. */ }
        }
    }
    private fun audioPermission():String =
        if(Build.VERSION.SDK_INT>=33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    private fun ensureMusicAccess() {
        if(Build.VERSION.SDK_INT>=23 && checkSelfPermission(audioPermission())!=PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(audioPermission()),72)
        } else startLocalMusic()
    }
    override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<out String>,grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if(requestCode==moonLocationPermission) { showMoonNow(); return }
        if(requestCode==72) {
            if(grantResults.firstOrNull()==PackageManager.PERMISSION_GRANTED) startLocalMusic()
            else AlertDialog.Builder(this).setTitle("Доступ к музыке")
                .setMessage("Разрешите доступ к аудиофайлам в настройках приложения. Без этого MP3 не прочитать.")
                .setPositiveButton("Повторить") { _, _ -> ensureMusicAccess() }
                .setNegativeButton("Позже",null).show()
        }
    }
    private fun startLocalMusic() {
        try {
            loadLocalMusic()
            if(tracks.isEmpty()) {
                AlertDialog.Builder(this).setTitle("MP3 не найдены")
                    .setMessage("Положите MP3 во внутреннюю память: Music/Tun (без вложенных папок). Затем нажмите «Повторить».")
                    .setPositiveButton("Повторить") { _, _ -> startLocalMusic() }
                    .setNegativeButton("Позже",null).show()
            } else {
                queue.clear()
                Toast.makeText(this,"Найдено MP3: ${tracks.size}",Toast.LENGTH_LONG).show()
                playNext()
            }
        } catch(e:Exception) {
            AlertDialog.Builder(this).setTitle("Ошибка поиска MP3")
                .setMessage(e.message ?: "Не удалось прочитать музыку")
                .setPositiveButton("Повторить") { _, _ -> startLocalMusic() }
                .setNegativeButton("Позже",null).show()
        }
    }
    private fun loadLocalMusic() {
        val found=mutableListOf<Uri>()
        val collection=if(Build.VERSION.SDK_INT>=29) MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val columns=arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.RELATIVE_PATH)
        val projection=if(Build.VERSION.SDK_INT>=29) columns else columns.take(2).toTypedArray()
        contentResolver.query(collection,projection,null,null,null)?.use { cursor ->
            val idColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val pathColumn=if(Build.VERSION.SDK_INT>=29) cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH) else -1
            while(cursor.moveToNext()) {
                val name=cursor.getString(nameColumn) ?: continue
                if(!name.endsWith(".mp3",true)) continue
                val id=cursor.getLong(idColumn)
                if(pathColumn>=0) {
                    val relative=(cursor.getString(pathColumn) ?: "").trim('/').lowercase()
                    if(relative!="music/tun") continue
                }
                found.add(android.content.ContentUris.withAppendedId(collection,id))
            }
        }
        if(Build.VERSION.SDK_INT<29) {
            found.clear()
            val folder=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),"Tun")
            folder.listFiles()?.filter { it.isFile && it.extension.equals("mp3",true) }?.forEach { found.add(Uri.fromFile(it)) }
        }
        tracks=found.distinct().toMutableList()
    }
    private fun promptFolder(message:String="Выберите папку Music/Tun с MP3 и разрешите доступ.") {
        AlertDialog.Builder(this).setTitle("Музыка для Бесік жыры").setMessage(message)
            .setPositiveButton("Выбрать папку") { _, _ -> chooseFolder() }
            .setNegativeButton("Позже",null).show()
    }
    private fun chooseFolder() {
        val intent=Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivityForResult(intent, 41)
    }
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        if(requestCode==41 && resultCode==RESULT_OK && data?.data!=null) {
            val uri=data.data!!
            try {
                contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                prefs.edit().putString("folder",uri.toString()).apply()
                loadFolder(uri)
                queue.clear()
                if(tracks.isEmpty()) promptFolder("В этой папке MP3 не найдены. Выберите Music/Tun.")
                else { Toast.makeText(this,"Найдено MP3: ${tracks.size}",Toast.LENGTH_LONG).show(); playNext() }
            } catch(e:Exception) { promptFolder("Папка недоступна: ${e.message}") }
        }
    }
    private fun loadFolder(folder:Uri) {
        val id=DocumentsContract.getTreeDocumentId(folder)
        val children=DocumentsContract.buildChildDocumentsUriUsingTree(folder,id)
        val result=mutableListOf<Uri>()
        val docs=contentResolver.query(children,arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID,DocumentsContract.Document.COLUMN_DISPLAY_NAME,DocumentsContract.Document.COLUMN_MIME_TYPE),null,null,null)
            ?: throw IllegalStateException("Не удалось прочитать папку")
        docs.use { cursor ->
            while(cursor.moveToNext()) {
                val docId=cursor.getString(0)
                val name=cursor.getString(1) ?: ""
                val mime=cursor.getString(2) ?: ""
                if(name.endsWith(".mp3",true) || mime=="audio/mpeg") result.add(DocumentsContract.buildDocumentUriUsingTree(folder,docId))
            }
        }
        tracks=result
    }
    private fun refill() {
        try { loadLocalMusic() } catch (_:Exception) {}
        queue=tracks.shuffled(random).toMutableList()
        if(queue.size>1 && queue.first()==current) queue.add(queue.removeAt(0))
    }
    private fun playNext() {
        if(queue.isEmpty()) refill()
        if(queue.isEmpty()) { startLocalMusic(); return }
        val next=queue.removeAt(0)
        play(next,true)
    }
    private var consecutiveFailures=0
    private fun play(uri:Uri,record:Boolean) {
        try {
            player?.release()
            val mp=MediaPlayer()
            mp.setDataSource(this,uri)
            mp.setOnCompletionListener { playNext() }
            mp.prepare()
            val remaining=(startedAt+sessionMs-SystemClock.elapsedRealtime()).coerceAtLeast(0L)
            val volume=if(remaining>=fadeMs) 1f else (remaining.toFloat()/fadeMs).coerceIn(0f,1f)
            mp.setVolume(volume,volume)
            mp.start()
            consecutiveFailures=0
            player=mp
            current=uri
            if(record) history.add(uri)
        } catch (e:Exception) {
            player?.release(); player=null
            consecutiveFailures++
            if(consecutiveFailures<3 && queue.isNotEmpty()) playNext()
            else { consecutiveFailures=0; AlertDialog.Builder(this).setTitle("Ошибка MP3").setMessage(e.message ?: "Не удалось воспроизвести файл").setPositiveButton("OK",null).show() }
        }
    }
    override fun onKeyDown(keyCode:Int,event:KeyEvent?):Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_MENU -> { startLocalMusic(); return true }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                if(player==null) ensureMusicAccess() else showMoonWithLocation()
                return true
            }
            KeyEvent.KEYCODE_DPAD_UP -> { sky.requestNextSkyObject(); return true }
            KeyEvent.KEYCODE_DPAD_DOWN -> { sky.toggleMilkyWay(); return true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { playNext(); sky.advance(); return true }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if(history.size>1) { history.removeAt(history.lastIndex); val prev=history.last(); play(prev,false) }
                sky.previous()
                return true
            }
        }
        return super.onKeyDown(keyCode,event)
    }
    override fun onDestroy() {
        updater.close()
        handler.removeCallbacks(stop)
        handler.removeCallbacks(fadeAudio)
        player?.release()
        player=null
        super.onDestroy()
    }
}

private data class Star(val x:Float,val y:Float,val size:Float,val alpha:Float,val phase:Float,val period:Float,val warm:Boolean)
class SkyView(context:Context): View(context) {
    private val rng=Random(SystemClock.elapsedRealtimeNanos())
    private val paint=Paint(Paint.ANTI_ALIAS_FLAG)
    // Keep star density consistent from 1080p through native 4K.
    private var stars: List<Star> = emptyList()
    override fun onSizeChanged(w:Int,h:Int,oldw:Int,oldh:Int) {
        super.onSizeChanged(w,h,oldw,oldh)
        val pixels = w.toLong() * h.toLong()
        val count = (1250.0 * pixels / (1920.0 * 1080.0)).toInt().coerceIn(600,5000)
        stars = List(count) {
        Star(rng.nextFloat(),rng.nextFloat(),0.45f+rng.nextFloat()*1.5f,
            0.12f+rng.nextFloat()*0.65f,rng.nextFloat()*6.283f,
            40f+rng.nextFloat()*55f,rng.nextFloat()<0.15f)
        }
    }
    private var milkyWayEnabled=false
    private var milkyTransitionAt=SystemClock.elapsedRealtime()
    private var milkyFrom=0f
    private var milkyTo=0f
    private val milkyFadeDuration=12000L
    private data class Dust(val x:Float,val y:Float,val radius:Float,val alpha:Int)
    private var dust:List<Dust> = emptyList()
    private var milkySlope=0f
    private var milkyOffset=.5f
    private var sessionEnd=Long.MAX_VALUE
    private var sessionFadeMs=300000L
    fun setSessionEnd(end:Long,fade:Long) { sessionEnd=end; sessionFadeMs=fade }
    private fun milkyOpacity(now:Long):Float {
        val p=((now-milkyTransitionAt).toFloat()/milkyFadeDuration).coerceIn(0f,1f)
        val smooth=p*p*(3f-2f*p)
        return milkyFrom+(milkyTo-milkyFrom)*smooth
    }
    fun toggleMilkyWay() {
        val now=SystemClock.elapsedRealtime()
        milkyFrom=milkyOpacity(now)
        milkyWayEnabled=!milkyWayEnabled
        milkyTo=if(milkyWayEnabled) 1f else 0f
        milkyTransitionAt=now
        if(milkyWayEnabled) {
            milkySlope=(rng.nextFloat()-.5f)*.8f
            milkyOffset=.38f+rng.nextFloat()*.24f
            dust=List(4200) {
                val x=rng.nextFloat()
                val spread=(rng.nextFloat()+rng.nextFloat()+rng.nextFloat()-1.5f)*.21f
                Dust(x,(milkyOffset+milkySlope*(x-.5f)+spread).coerceIn(0f,1f),
                    .32f+rng.nextFloat()*.95f,7+rng.nextInt(19))
            }
        }
        invalidate()
    }
    private fun drawMilkyWay(canvas:Canvas,now:Long,w:Float,h:Float) {
        val opacity=milkyOpacity(now)
        if(opacity<=0f) return
        // Low-contrast soft band plus individually randomized stars.
        val centerY=(milkyOffset+milkySlope*(.5f-.5f))*h
        canvas.save()
        canvas.rotate(kotlin.math.atan2(milkySlope*h,w)*180f/Math.PI.toFloat(),w*.5f,centerY)
        glow.shader=LinearGradient(0f,centerY-h*.23f,0f,centerY+h*.23f,
            intArrayOf(Color.TRANSPARENT,Color.argb((opacity*8).toInt(),65,85,118),
                Color.argb((opacity*13).toInt(),65,82,110),
                Color.argb((opacity*18).toInt(),65,85,118),Color.TRANSPARENT),
            null,Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,glow)
        glow.shader=null
        canvas.restore()
        // Exactly one of 20 interleaved cohorts (5%) can twinkle at a time.
        // Each star returns to its original brightness before the cohort changes.
        val twinkleElapsed=(now-milkyTransitionAt).coerceAtLeast(0L)
        val cohort=((twinkleElapsed/20000L)%20L).toInt()
        val cycleMs=(twinkleElapsed%20000L).toFloat()
        paint.color=Color.rgb(194,211,233)
        for(i in dust.indices) {
            val d=dust[i]
            var brightness=1f
            if(i%20==cohort) {
                // Stagger starts over 8 seconds; a smooth 12-second dip follows.
                val delay=((i/20*137)%8000).toFloat()
                val p=((cycleMs-delay)/12000f).coerceIn(0f,1f)
                val dip=sin(Math.PI*p.toDouble()).toFloat()
                brightness=1f-.92f*dip*dip
            }
            paint.alpha=(d.alpha*opacity*brightness).toInt().coerceIn(0,255)
            canvas.drawCircle(d.x*w,d.y*h,(d.radius*w/1920f).coerceAtLeast(.45f),paint)
        }
    }
    private data class Satellite(val start:Long,val duration:Long,val x:Float,val y:Float,
        val dx:Float,val dy:Float,val radius:Float,val brightness:Int,val twinkles:Boolean,val color:Int)
    private val satellites=mutableListOf<Satellite>()
    private var satelliteCount=0
    private var nextSatelliteAt=SystemClock.elapsedRealtime()+rng.nextLong(60000L,180001L)
    private fun satelliteColor():Int = when(rng.nextInt(100)) {
        in 0..74 -> Color.rgb(220,227,236)
        in 75..89 -> Color.rgb(202,218,240)
        else -> Color.rgb(240,226,202)
    }
    private fun launchSatellite(now:Long) {
        val dx=(if(rng.nextBoolean()) 1f else -1f)*(.85f+rng.nextFloat()*.25f)
        satelliteCount++
        satellites.add(Satellite(now,rng.nextLong(110000L,190001L),
            if(dx>0) -.06f else 1.06f,.12f+rng.nextFloat()*.76f,
            dx,(rng.nextFloat()-.5f)*.18f,
            1.2f+rng.nextFloat()*1.15f,110+rng.nextInt(75),
            satelliteCount%2==0,satelliteColor()))
    }
    private var upPressCount=0
    fun requestNextSkyObject() {
        upPressCount++
        if(upPressCount%2==1) requestComet()
        else postDelayed({ launchSatellite(SystemClock.elapsedRealtime()); invalidate() },5000L)
    }
    private fun drawSatellite(canvas:Canvas,now:Long,w:Float,h:Float) {
        if(now>=nextSatelliteAt) {
            launchSatellite(now)
            nextSatelliteAt=now+rng.nextLong(60000L,180001L)
        }
        val it=satellites.iterator()
        while(it.hasNext()) {
            val sat=it.next()
            val progress=(now-sat.start).toFloat()/sat.duration
            if(progress>=1f) { it.remove(); continue }
            val visibility=(min(1f,progress*12f)*min(1f,(1f-progress)*12f)).coerceIn(0f,1f)
            paint.color=sat.color
            val twinkle=if(sat.twinkles) (0.7f+0.3f*kotlin.math.cos((now-sat.start)/1000f*1.4f)) else 1f
            paint.alpha=(visibility*sat.brightness*twinkle).toInt().coerceIn(0,255)
            canvas.drawCircle((sat.x+sat.dx*progress)*w,(sat.y+sat.dy*progress)*h,
                (sat.radius*w/1920f).coerceAtLeast(.9f),paint)
        }
    }
    private val order=catalog.indices.shuffled().toMutableList()
    private var index=0
    private var sceneStart=SystemClock.elapsedRealtime()
    private val appLaunchAt=sceneStart
    private var firstConstellationChanged=false
    private var outgoing:Int?=null
    private var transitionStart=0L
    private val transitionDuration=18000L
    // Rare, dim shooting stars: randomized path and 1–3 minute gaps.
    private data class Comet(val start:Long,val duration:Long,val x:Float,val y:Float,
        val dx:Float,val dy:Float,val brightness:Float,val tail:Float)
    private var comet:Comet?=null
    private var nextCometAt=SystemClock.elapsedRealtime()+rng.nextLong(60000L,180001L)
    fun requestComet() {
        // A new UP press schedules a random comet five seconds later.
        // The existing sky, music and constellation controls are unchanged.
        nextCometAt=SystemClock.elapsedRealtime()+5000L
        invalidate()
    }
    private val cometPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style=Paint.Style.STROKE
        strokeCap=Paint.Cap.ROUND
    }
    private fun drawComet(canvas:Canvas,now:Long,w:Float,h:Float) {
        if(comet==null && now>=nextCometAt) {
            val angle=(if(rng.nextBoolean()) 1f else -1f)*(0.25f+rng.nextFloat()*0.65f)
            val dx=(0.12f+rng.nextFloat()*0.16f)*(if(rng.nextBoolean()) 1f else -1f)
            val dy=dx*angle
            comet=Comet(now,rng.nextLong(4500L,7001L),
                0.12f+rng.nextFloat()*0.70f,0.15f+rng.nextFloat()*0.65f,
                dx,dy,0.48f+rng.nextFloat()*0.22f,0.018f+rng.nextFloat()*0.022f)
        }
        val c=comet ?: return
        val p=(now-c.start).toFloat()/c.duration
        if(p>=1f) {
            comet=null
            if(nextCometAt<=c.start) nextCometAt=now+rng.nextLong(60000L,180001L)
            return
        }
        val envelope=(min(1f,p*5f)*min(1f,(1f-p)*5f)).coerceIn(0f,1f)
        val x=(c.x+c.dx*p)*w
        val y=(c.y+c.dy*p)*h
        val magnitude=sqrt((c.dx*w)*(c.dx*w)+(c.dy*h)*(c.dy*h)).coerceAtLeast(1f)
        val tailLength=c.tail*w
        val tx=x-c.dx*w/magnitude*tailLength
        val ty=y-c.dy*h/magnitude*tailLength
        val alpha=(c.brightness*envelope*255f).toInt().coerceIn(0,255)
        cometPaint.strokeWidth=(1.35f*w/1920f).coerceAtLeast(1.0f)
        cometPaint.shader=LinearGradient(tx,ty,x,y,
            intArrayOf(Color.TRANSPARENT,Color.argb(alpha/2,170,196,230),
                Color.argb(alpha,205,222,246)),null,Shader.TileMode.CLAMP)
        canvas.drawLine(tx,ty,x,y,cometPaint)
        cometPaint.shader=null
        paint.color=Color.rgb(205,222,246)
        paint.alpha=alpha
        canvas.drawCircle(x,y,(1.5f*w/1920f).coerceAtLeast(1.1f),paint)
    }
    // Cached, locally rendered lunar surface. No network or per-frame texture allocation.
    private var moonShownAt=-30000L
    private var moonLatitude:Double?=null
    private var moonBitmap:Bitmap?=null
    private val moonDisplayPaint=Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    fun showMoon(latitude:Double?,longitude:Double?) {
        moonLatitude=latitude
        moonBitmap=createMoonBitmap()
        moonShownAt=SystemClock.elapsedRealtime()
        invalidate()
    }
    private fun lunarNoise(x:Double,y:Double,scale:Double):Double {
        val px=x*scale; val py=y*scale
        val ix=floor(px).toInt(); val iy=floor(py).toInt()
        val fx=px-floor(px); val fy=py-floor(py)
        val tx=fx*fx*(3.0-2.0*fx); val ty=fy*fy*(3.0-2.0*fy)
        fun hash(a:Int,b:Int):Double {
            var h=a*374761393+b*668265263+0x27d4eb2d
            h=(h xor (h ushr 13))*1274126177
            return ((h xor (h ushr 16)).toLong() and 0x7fffffffL).toDouble()/2147483647.0
        }
        val top=hash(ix,iy)*(1.0-tx)+hash(ix+1,iy)*tx
        val bottom=hash(ix,iy+1)*(1.0-tx)+hash(ix+1,iy+1)*tx
        return top*(1.0-ty)+bottom*ty
    }
    private fun createMoonBitmap():Bitmap {
        val n=256
        val bitmap=Bitmap.createBitmap(n,n,Bitmap.Config.ARGB_8888)
        val pixels=IntArray(n*n)
        val phase=((System.currentTimeMillis()-947182440000.0)/86400000.0/29.530588853).let { it-floor(it) }
        // Solar direction relative to the visible lunar disc, phase 0=new, 0.5=full.
        val sunX=sin(2.0*Math.PI*phase)
        val sunZ=-cos(2.0*Math.PI*phase)
        val maria=arrayOf(
            doubleArrayOf(-.28,-.20,.28,.19,.30),doubleArrayOf(.18,-.31,.22,.25,.27),
            doubleArrayOf(.40,.06,.17,.25,.24),doubleArrayOf(-.07,.28,.22,.18,.21),
            doubleArrayOf(-.42,.28,.18,.15,.16),doubleArrayOf(.04,-.02,.12,.16,.12)
        )
        val craters=Array(110) { i ->
            val angle=i*2.399963229728653
            val distance=sqrt((i+.5)/110.0)*.94
            val x=cos(angle)*distance; val y=sin(angle)*distance
            val radius=.008+(lunarNoise(i.toDouble(),2.0,1.0)*.033)
            doubleArrayOf(x,y,radius)
        }
        for(iy in 0 until n) for(ix in 0 until n) {
            val x=(ix-(n-1)/2.0)/(n*.47)
            val y=(iy-(n-1)/2.0)/(n*.47)
            val r2=x*x+y*y
            if(r2>1.0) continue
            val z=sqrt(1.0-r2)
            var terrain=0.63+0.12*(lunarNoise(x,y,5.0)-.5)+
                0.09*(lunarNoise(x,y,16.0)-.5)+0.05*(lunarNoise(x,y,48.0)-.5)
            for(m in maria) {
                val dx=(x-m[0])/m[2]; val dy=(y-m[1])/m[3]
                val d=dx*dx+dy*dy
                terrain-=m[4]*exp(-d*1.7)
            }
            for(c in craters) {
                val dx=x-c[0]; val dy=y-c[1]
                val distance=sqrt(dx*dx+dy*dy)/c[2]
                if(distance<1.8) {
                    terrain+=.075*exp(-((distance-1.0)*(distance-1.0))/.055)
                    terrain-=.055*exp(-distance*distance*2.2)
                    terrain+=.025*(dx/c[2])*exp(-((distance-1.0)*(distance-1.0))/.08)
                }
            }
            val light=(x*sunX+z*sunZ).coerceAtLeast(0.0)
            // Low ambient reflected earthlight preserves a faint dark hemisphere.
            val brightness=terrain*(.035+.965*sqrt(light))*z.pow(.12)
            val value=(brightness*255.0).toInt().coerceIn(0,255)
            pixels[iy*n+ix]=Color.argb(255,value,(value*.99).toInt(),(value*.95).toInt())
        }
        bitmap.setPixels(pixels,0,n,0,0,n,n)
        return bitmap
    }
    private fun drawMoon(canvas:Canvas,now:Long,w:Float,h:Float) {
        val elapsed=now-moonShownAt
        if(elapsed<0L || elapsed>=20000L) return
        val bitmap=moonBitmap ?: return
        val fadeIn=(elapsed/3000f).coerceIn(0f,1f)
        val fadeOut=((20000L-elapsed)/5000f).coerceIn(0f,1f)
        val opacity=(fadeIn*fadeIn*(3f-2f*fadeIn)*fadeOut).coerceIn(0f,1f)
        val radius=min(w*.037f,h*.068f).coerceAtLeast(10f)
        val cx=w*.932f; val cy=h*.115f
        val save=canvas.save()
        // Southern hemisphere reverses the conventional north-up appearance.
        canvas.rotate(if((moonLatitude ?: 0.0)<0.0) 180f else 0f,cx,cy)
        moonDisplayPaint.alpha=(opacity*255).toInt().coerceIn(0,255)
        canvas.drawBitmap(bitmap,null,RectF(cx-radius,cy-radius,cx+radius,cy+radius),moonDisplayPaint)
        canvas.restoreToCount(save)
    }
    private val bg=Paint(Paint.ANTI_ALIAS_FLAG)
    private val glow=Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply { color=Color.rgb(159,177,207); typeface=Typeface.create("sans-serif-light",Typeface.NORMAL) }
    private val linePaint=Paint(Paint.ANTI_ALIAS_FLAG).apply { color=Color.rgb(126,165,218); strokeWidth=1.1f; style=Paint.Style.STROKE }
    fun advance() { change(1) }
    fun previous() { change(-1) }
    private fun change(direction:Int) {
        firstConstellationChanged=true
        outgoing=order[index]
        index+=direction
        if(index>=order.size) { order.shuffle(); index=0 }
        if(index<0) index=order.lastIndex
        sceneStart=SystemClock.elapsedRealtime()
        transitionStart=sceneStart
        invalidate()
    }
    private fun drawConstellation(canvas:Canvas,item:Int,opacity:Float,labelOpacity:Float,lineOpacity:Float=labelOpacity) {
        if(opacity<=0f) return
        val w=width.toFloat(); val h=height.toFloat()
        val c=catalog[item]
        val scale=min(w*.58f,h*.75f)
        val originX=w*.5f-scale*.5f; val originY=h*.5f-scale*.5f
        fun xy(p:Pair<Float,Float>)=Pair(originX+p.first*scale,originY+p.second*scale)
        linePaint.strokeWidth=(1.1f*w/1920f).coerceAtLeast(0.75f)
        linePaint.alpha=(lineOpacity*105).toInt().coerceIn(0,255)
        for((a,b) in c.edges) {
            val p=xy(c.points[a]); val q=xy(c.points[b])
            canvas.drawLine(p.first,p.second,q.first,q.second,linePaint)
        }
        for((i,p) in c.points.withIndex()) {
            val pos=xy(p)
            val radius=(if(i==0) 3.4f else 2.5f+(i%3)*.35f)*w/1920f
            paint.color=Color.rgb(226,236,255)
            paint.alpha=(opacity*230).toInt().coerceIn(0,255)
            canvas.drawCircle(pos.first,pos.second,radius,paint)
        }
        if(labelOpacity>0f) {
            textPaint.textSize=28f*w/1920f
            textPaint.alpha=(labelOpacity*140).toInt().coerceIn(0,255)
            val label=if(c.kz.equals(c.ru,ignoreCase=true)) "${c.kz} - ${c.en}" else "${c.kz} - ${c.en} - ${c.ru}"
            canvas.drawText(label,w*.025f,h*.95f,textPaint)
        }
    }
    override fun onDraw(canvas:Canvas) {
        super.onDraw(canvas)
        val w=width.toFloat(); val h=height.toFloat()
        val now=SystemClock.elapsedRealtime()
        if(now-sceneStart>=120000L) advance()
        val seconds=(now-sceneStart)/1000f
        val t=now/1000f
        bg.shader=LinearGradient(0f,0f,w,h, intArrayOf(Color.rgb(2,5,15),Color.rgb(5,12,29),Color.rgb(1,3,11)),null,Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,bg)
        bg.shader=null
        // Very subtle, low-contrast Milky Way haze, never bright enough to disturb sleep.
        glow.shader=RadialGradient(w*.54f,h*.48f,w*.62f,intArrayOf(Color.argb(25,35,57,100),Color.argb(8,21,43,80),Color.TRANSPARENT),null,Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,glow)
        glow.shader=null
        drawMilkyWay(canvas,now,w,h)
        // Every two minutes a different ~5% cohort of background stars
        // slowly vanishes completely and returns. The other 95% keep
        // their existing independent, subtle breathing animation.
        val fadeCycle=(now/120000L).toInt()
        val fadeSeconds=(now%120000L)/1000f
        val fadeGroup=fadeCycle%20
        for((starIndex,s) in stars.withIndex()) {
            val breathing=(0.5f+0.5f*cos((t/s.period)*6.283185f+s.phase)).toFloat()
            val baseAlpha=(s.alpha*(0.28f+0.72f*breathing)).coerceIn(0f,1f)
            val fadeFactor=if(starIndex%20==fadeGroup) {
                when {
                    fadeSeconds<50f -> {
                        val p=fadeSeconds/50f
                        1f-p*p*(3f-2f*p)
                    }
                    fadeSeconds<70f -> 0f
                    fadeSeconds<120f -> {
                        val p=(fadeSeconds-70f)/50f
                        p*p*(3f-2f*p)
                    }
                    else -> 1f
                }
            } else 1f
            val a=baseAlpha*fadeFactor
            val x=s.x*w; val y=s.y*h
            paint.color=when {
                s.warm -> Color.rgb(242,224,207)
                starIndex%17==0 -> Color.rgb(255,231,198)
                starIndex%11==0 -> Color.rgb(171,199,244)
                starIndex%7==0 -> Color.rgb(238,240,246)
                else -> Color.rgb(207,222,246)
            }
            paint.alpha=(a*255).toInt()
            canvas.drawCircle(x,y,s.size*w/1920f,paint)
        }
        drawComet(canvas,now,w,h)
        drawSatellite(canvas,now,w,h)
        drawMoon(canvas,now,w,h)
        val elapsed=now-transitionStart
        val transitioning=outgoing!=null && elapsed<transitionDuration
        if(transitioning) {
            val half=transitionDuration/2f
            if(elapsed<half) {
                val p=(elapsed/half).coerceIn(0f,1f)
                val eased=p*p*(3f-2f*p)
                drawConstellation(canvas,outgoing!!,1f-eased,0f,0f)
            } else {
                val p=((elapsed-half)/half).coerceIn(0f,1f)
                val eased=p*p*(3f-2f*p)
                drawConstellation(canvas,order[index],eased,eased)
            }
        } else {
            outgoing=null
            // Lines and label remain for 11 seconds after the new constellation
            // is fully visible, then fade together over the next 4 seconds.
            val fullyVisibleAt=if(transitionStart==0L) sceneStart else transitionStart+transitionDuration
            val visibleSeconds=((now-fullyVisibleAt).coerceAtLeast(0L))/1000f
            val guideOpacity=if(visibleSeconds<11f) 1f else ((15f-visibleSeconds)/4f).coerceIn(0f,1f)
            drawConstellation(canvas,order[index],1f,guideOpacity,guideOpacity)
        }
        // Creator credit appears only on the initial constellation, on the opposite side.
        // It fades away with that constellation's first label and never returns.
        if(!firstConstellationChanged) {
            val secondsSinceLaunch=(now-appLaunchAt).coerceAtLeast(0L)/1000f
            val fadeIn=(secondsSinceLaunch/5f).coerceIn(0f,1f)
            val fadeOut=((15f-secondsSinceLaunch)/4f).coerceIn(0f,1f)
            val creditOpacity=fadeIn*fadeIn*(3f-2f*fadeIn)*fadeOut
            if(creditOpacity>0f) {
                val credit="Created by Nurtaza Saparov"
                textPaint.textSize=28f*w/1920f
                textPaint.alpha=(creditOpacity*140).toInt().coerceIn(0,255)
                canvas.drawText(credit,w*.975f-textPaint.measureText(credit),h*.95f,textPaint)
            }
        }
        // Dim the entire sky during the final five minutes.
        val remaining=sessionEnd-now
        if(remaining<sessionFadeMs) {
            val opacity=(1f-remaining.toFloat()/sessionFadeMs).coerceIn(0f,1f)
            paint.color=Color.BLACK
            paint.alpha=(opacity*255f).toInt()
            canvas.drawRect(0f,0f,w,h,paint)
        }
        // Refresh at display cadence; actual frame rate depends on projector hardware.
        postInvalidateDelayed(100L)
    }
}
