Tesztelni localhoston:
	1: Elindítod a gépeden a servert: java -jar ECServer_2016_1.0.jar
	2: Kikeresed a géped ip-címét a lanon belül
	3: Beírod az app-ban a "Used ip-hez"

TODO:
	A) Image processing javítása. Ez jelenleg nem jó elmosódott képeknél, mint a 15-ös vagy a 17-es.
	  1. Kéne egy util, amit az ImageUtils.cleanImage előtt futtatunk le és kiélesíti a képet.
	  2. Utána ennek megffelelően belőni a thresholdot. Én 100 körül látttam jónak, csak az meg elrontotta az előző pontban lévő két képet. Ha az megoldódna, ezt be lehetne lőni.
	  3. Ezután belőni a bitmap méretét. Jelenleg a byte tömb dekódolásánál van megadva, hogy mennyit kicsinyítsen, de az túl kicsi lesz és független a bitmap felbontásától.
	B) valami egyszerű toolt (akár python script, akármi), ami a result.txt-t összehasonlítja a mintamegoldással.
