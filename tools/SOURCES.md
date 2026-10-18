# Sources of the card art

Every card is from one set: scans of an original 1909 "Roses & Lilies" printing of the
Rider-Waite-Smith deck, drawn by Pamela Colman Smith (1878-1951), scanned by Saskia Jansen from
her own deck and uploaded to Wikimedia Commons in the category
[Rider-Waite tarot deck (Roses & Lilies)](https://commons.wikimedia.org/wiki/Category:Rider-Waite_tarot_deck_(Roses_%26_Lilies)).
Each file page carries `{{PD-old-70-expired}}`: published 1909, and the artist died in 1951.

`fetch-scans.py` downloads them and checks each against the SHA-1 below; `make-art.py` turns them
into the app's `card_<id>.webp` and `thumb_<id>.webp`.

| id | Commons file | size | SHA-1 |
|---|---|---|---|
| m00 | [RWS1909 - 00 Fool.jpeg](https://upload.wikimedia.org/wikipedia/commons/6/6e/RWS1909_-_00_Fool.jpeg) | 825x1429 | `287b2042278e1eff5066a6cef115a1a409384add` |
| m01 | [RWS1909 - 01 Magician.jpeg](https://upload.wikimedia.org/wikipedia/commons/b/b0/RWS1909_-_01_Magician.jpeg) | 849x1429 | `631db8c7e7f5a258637f9f3933fbefe861672385` |
| m02 | [RWS1909 - 02 High Priestess.jpeg](https://upload.wikimedia.org/wikipedia/commons/2/26/RWS1909_-_02_High_Priestess.jpeg) | 819x1451 | `672876c94d85b74c15c5421af9ef70ea7f1d7a70` |
| m03 | [RWS1909 - 03 Empress.jpeg](https://upload.wikimedia.org/wikipedia/commons/7/70/RWS1909_-_03_Empress.jpeg) | 860x1455 | `765a2bf614970581a46cd7094a8d74cce20c6d42` |
| m04 | [RWS1909 - 04 Emperor.jpeg](https://upload.wikimedia.org/wikipedia/commons/b/b4/RWS1909_-_04_Emperor.jpeg) | 816x1417 | `e8325d99312ed62ddac51dbd4948491637a6c142` |
| m05 | [RWS1909 - 05 Hierophant.jpeg](https://upload.wikimedia.org/wikipedia/commons/7/70/RWS1909_-_05_Hierophant.jpeg) | 832x1416 | `c35324e6a9b6cdf12929b4ff7ca7f7fe6040ff83` |
| m06 | [RWS1909 - 06 Lovers.jpeg](https://upload.wikimedia.org/wikipedia/commons/8/87/RWS1909_-_06_Lovers.jpeg) | 814x1430 | `1d65ad5c64962e5633949d306367962a93f7bf82` |
| m07 | [RWS1909 - 07 Chariot.jpeg](https://upload.wikimedia.org/wikipedia/commons/a/aa/RWS1909_-_07_Chariot.jpeg) | 838x1441 | `0323c1e2bcdef5b769cce22b0aee5ffc4d13460e` |
| m08 | [RWS1909 - 08 Strength.jpeg](https://upload.wikimedia.org/wikipedia/commons/2/29/RWS1909_-_08_Strength.jpeg) | 817x1413 | `129287de0ff801086720ef2ade2c1b943be1e390` |
| m09 | [RWS1909 - 09 Hermit.jpeg](https://upload.wikimedia.org/wikipedia/commons/2/28/RWS1909_-_09_Hermit.jpeg) | 842x1424 | `d5ec9f2644aeef10602dd76159275b4a52fa8bc6` |
| m10 | [RWS1909 - 10 Wheel of Fortune.jpeg](https://upload.wikimedia.org/wikipedia/commons/8/8a/RWS1909_-_10_Wheel_of_Fortune.jpeg) | 814x1437 | `aed5a856b71a0403d896d722691c83ef17642afb` |
| m11 | [RWS1909 - 11 Justice.jpeg](https://upload.wikimedia.org/wikipedia/commons/4/4f/RWS1909_-_11_Justice.jpeg) | 828x1441 | `64765d67ac8451b326a7a4ff8e3828f1bd4311ec` |
| m12 | [RWS1909 - 12 Hanged Man.jpeg](https://upload.wikimedia.org/wikipedia/commons/a/ab/RWS1909_-_12_Hanged_Man.jpeg) | 812x1423 | `febc002a69b63c07fd4251b1879655b97caf89e5` |
| m13 | [RWS1909 - 13 Death.jpeg](https://upload.wikimedia.org/wikipedia/commons/e/e9/RWS1909_-_13_Death.jpeg) | 831x1424 | `dcbbc5c8c3aa74ea1696221e5fb0e5c074f1a4b6` |
| m14 | [RWS1909 - 14 Temperance.jpeg](https://upload.wikimedia.org/wikipedia/commons/9/9e/RWS1909_-_14_Temperance.jpeg) | 826x1433 | `09c8636d8b45624b54bee550cfaad6f241facdd9` |
| m15 | [RWS1909 - 15 Devil.jpeg](https://upload.wikimedia.org/wikipedia/commons/2/26/RWS1909_-_15_Devil.jpeg) | 849x1453 | `b793a370055a9d8747c2d7160b9b6e0a6f1ac60e` |
| m16 | [RWS1909 - 16 Tower.jpeg](https://upload.wikimedia.org/wikipedia/commons/2/2a/RWS1909_-_16_Tower.jpeg) | 814x1424 | `ee4db996b152b43ea975fa00827b91cd23c3f6a5` |
| m17 | [RWS1909 - 17 Star.jpeg](https://upload.wikimedia.org/wikipedia/commons/4/44/RWS1909_-_17_Star.jpeg) | 839x1421 | `8fe3ff3e770ebd930ff381524a533d159a602d9a` |
| m18 | [RWS1909 - 18 Moon.jpeg](https://upload.wikimedia.org/wikipedia/commons/3/3d/RWS1909_-_18_Moon.jpeg) | 814x1441 | `e93a590ffb090fd1d065aae8df3d84521662fe70` |
| m19 | [RWS1909 - 19 Sun.jpeg](https://upload.wikimedia.org/wikipedia/commons/e/e1/RWS1909_-_19_Sun.jpeg) | 848x1459 | `256223bdc71483816a85d6ecd90848865eecd830` |
| m20 | [RWS1909 - 20 Judgement.jpeg](https://upload.wikimedia.org/wikipedia/commons/8/8b/RWS1909_-_20_Judgement.jpeg) | 832x1421 | `01c0116793f940a6419bcea94cd4dc2e2ccbc2cb` |
| m21 | [RWS1909 - 21 World.jpeg](https://upload.wikimedia.org/wikipedia/commons/a/a4/RWS1909_-_21_World.jpeg) | 833x1418 | `30bda621ac0788351812ee32dba33ebfd98ad8c1` |
| wands01 | [RWS1909 - Wands 01.jpeg](https://upload.wikimedia.org/wikipedia/commons/e/ee/RWS1909_-_Wands_01.jpeg) | 815x1448 | `2a6abe97af731ea48dc6d9c2d32eccb0e31381ad` |
| wands02 | [RWS1909 - Wands 02.jpeg](https://upload.wikimedia.org/wikipedia/commons/0/06/RWS1909_-_Wands_02.jpeg) | 844x1435 | `8996d54b2ddf63403405ff651274315cbcd36592` |
| wands03 | [RWS1909 - Wands 03.jpeg](https://upload.wikimedia.org/wikipedia/commons/5/50/RWS1909_-_Wands_03.jpeg) | 816x1414 | `d057c4173044c69b083c9f72c05db86249fcf2d4` |
| wands04 | [RWS1909 - Wands 04.jpeg](https://upload.wikimedia.org/wikipedia/commons/b/b5/RWS1909_-_Wands_04.jpeg) | 836x1428 | `be796bf88a4db62f95ea829450250530739362cc` |
| wands05 | [RWS1909 - Wands 05.jpeg](https://upload.wikimedia.org/wikipedia/commons/7/79/RWS1909_-_Wands_05.jpeg) | 815x1438 | `cc7ef5a5fe39c1c6accc417059d74f37ffa13e54` |
| wands06 | [RWS1909 - Wands 06.jpeg](https://upload.wikimedia.org/wikipedia/commons/2/2c/RWS1909_-_Wands_06.jpeg) | 843x1445 | `b0f94ceb1412f2e44380988bb0bc7c78ab690043` |
| wands07 | [RWS1909 - Wands 07.jpeg](https://upload.wikimedia.org/wikipedia/commons/1/18/RWS1909_-_Wands_07.jpeg) | 821x1425 | `62b66676acc61f82decd75b3456377450f483d50` |
| wands08 | [RWS1909 - Wands 08.jpeg](https://upload.wikimedia.org/wikipedia/commons/7/7e/RWS1909_-_Wands_08.jpeg) | 840x1425 | `c14ce9e3a5b996145e218a231fc4381c429ef4e6` |
| wands09 | [RWS1909 - Wands 09.jpeg](https://upload.wikimedia.org/wikipedia/commons/5/55/RWS1909_-_Wands_09.jpeg) | 819x1434 | `1a9c12d8bfbcbb7de4744db0ee6e56fb8bc8a240` |
| wands10 | [RWS1909 - Wands 10.jpeg](https://upload.wikimedia.org/wikipedia/commons/8/80/RWS1909_-_Wands_10.jpeg) | 839x1439 | `da85c787b930fd75cff0e5c322733917e5facdaa` |
| wands11 | [RWS1909 - Wands 11.jpeg](https://upload.wikimedia.org/wikipedia/commons/b/bd/RWS1909_-_Wands_11.jpeg) | 820x1423 | `596049095199989193bd97a6687bb9a10e67b8f4` |
| wands12 | [RWS1909 - Wands 12.jpeg](https://upload.wikimedia.org/wikipedia/commons/7/77/RWS1909_-_Wands_12.jpeg) | 833x1415 | `748838398108af84c8346a94b1aa3fdcb3ceb1a0` |
| wands13 | [RWS1909 - Wands 13.jpeg](https://upload.wikimedia.org/wikipedia/commons/5/50/RWS1909_-_Wands_13.jpeg) | 822x1446 | `11f1e480eca9badc28582a21f8b5c6f9353a3820` |
| wands14 | [RWS1909 - Wands 14.jpeg](https://upload.wikimedia.org/wikipedia/commons/6/62/RWS1909_-_Wands_14.jpeg) | 851x1441 | `86a68253d0fbf38c2d82281bd434ae5844b6722c` |
| cups01 | [RWS1909 - Cups 01.jpeg](https://upload.wikimedia.org/wikipedia/commons/9/92/RWS1909_-_Cups_01.jpeg) | 816x1418 | `a61029604527a63ab98d6544db94ea18ede16166` |
| cups02 | [RWS1909 - Cups 02.jpeg](https://upload.wikimedia.org/wikipedia/commons/4/47/RWS1909_-_Cups_02.jpeg) | 833x1413 | `1098be1e1118675abd630e26d06ff32dc2d9d339` |
| cups03 | [RWS1909 - Cups 03.jpeg](https://upload.wikimedia.org/wikipedia/commons/0/01/RWS1909_-_Cups_03.jpeg) | 818x1448 | `cc8a792e6f974fbdd83616b2b02db0ada977ddb8` |
| cups04 | [RWS1909 - Cups 04.jpeg](https://upload.wikimedia.org/wikipedia/commons/a/a6/RWS1909_-_Cups_04.jpeg) | 839x1433 | `33e65a2df226fa908508c99628ca3e47a66afa43` |
| cups05 | [RWS1909 - Cups 05.jpeg](https://upload.wikimedia.org/wikipedia/commons/1/15/RWS1909_-_Cups_05.jpeg) | 818x1430 | `638fa67baf2647e11b1f7a773999623b5257ce01` |
| cups06 | [RWS1909 - Cups 06.jpeg](https://upload.wikimedia.org/wikipedia/commons/6/68/RWS1909_-_Cups_06.jpeg) | 834x1424 | `1596149448f8a9ebce9eafeaa4b5735d37257309` |
| cups07 | [RWS1909 - Cups 07.jpeg](https://upload.wikimedia.org/wikipedia/commons/b/bb/RWS1909_-_Cups_07.jpeg) | 817x1451 | `ff9a9ff2a9c5f03bbc67c2e283c32e2dccdcb4e7` |
| cups08 | [RWS1909 - Cups 08.jpeg](https://upload.wikimedia.org/wikipedia/commons/f/f9/RWS1909_-_Cups_08.jpeg) | 830x1438 | `7c97829e6fe0d84d7f22b530d976f15b815639a9` |
| cups09 | [RWS1909 - Cups 09.jpeg](https://upload.wikimedia.org/wikipedia/commons/d/df/RWS1909_-_Cups_09.jpeg) | 818x1421 | `c0154ff5872aa37582669930ecaa992be460c11b` |
| cups10 | [RWS1909 - Cups 10.jpeg](https://upload.wikimedia.org/wikipedia/commons/0/0b/RWS1909_-_Cups_10.jpeg) | 841x1420 | `918e59c520aedff119e1b404ea24ed67ada227e1` |
| cups11 | [RWS1909 - Cups 11.jpeg](https://upload.wikimedia.org/wikipedia/commons/d/d4/RWS1909_-_Cups_11.jpeg) | 817x1436 | `3a51d2e1b0cf4115485136a9f818ee4f37c4e201` |
| cups12 | [RWS1909 - Cups 12.jpeg](https://upload.wikimedia.org/wikipedia/commons/0/06/RWS1909_-_Cups_12.jpeg) | 836x1436 | `227eaddf702f27ae24053ba81a09425629ba6013` |
| cups13 | [RWS1909 - Cups 13.jpeg](https://upload.wikimedia.org/wikipedia/commons/9/91/RWS1909_-_Cups_13.jpeg) | 820x1425 | `18eadde622eadbd4ac8c97f74a79fc6a2a48068a` |
| cups14 | [RWS1909 - Cups 14.jpeg](https://upload.wikimedia.org/wikipedia/commons/b/be/RWS1909_-_Cups_14.jpeg) | 838x1424 | `e13c60e05de19ce5884d800a52aa0095eb39e4e1` |
| swords01 | [RWS1909 - Swords 01.jpeg](https://upload.wikimedia.org/wikipedia/commons/8/87/RWS1909_-_Swords_01.jpeg) | 826x1443 | `cf270e15237246403254bf1b3a766ae94c49f253` |
| swords02 | [RWS1909 - Swords 02.jpeg](https://upload.wikimedia.org/wikipedia/commons/d/da/RWS1909_-_Swords_02.jpeg) | 839x1432 | `2d9586f8da5e70a9e5d888b969745a478bc8a87c` |
| swords03 | [RWS1909 - Swords 03.jpeg](https://upload.wikimedia.org/wikipedia/commons/0/01/RWS1909_-_Swords_03.jpeg) | 818x1418 | `b52a95f47bbe613f7bcd52253173edffb54a3bd0` |
| swords04 | [RWS1909 - Swords 04.jpeg](https://upload.wikimedia.org/wikipedia/commons/d/d9/RWS1909_-_Swords_04.jpeg) | 834x1418 | `00885a5e82a50798bf4741fd8b8b2be3b4976d26` |
| swords05 | [RWS1909 - Swords 05.jpeg](https://upload.wikimedia.org/wikipedia/commons/b/bb/RWS1909_-_Swords_05.jpeg) | 816x1434 | `a9000d217da7f818f0311afbf2ca7fd5975e6249` |
| swords06 | [RWS1909 - Swords 06.jpeg](https://upload.wikimedia.org/wikipedia/commons/a/a6/RWS1909_-_Swords_06.jpeg) | 842x1440 | `408f6a2dd6ab0a8aa51a8e7ce606f3627679e383` |
| swords07 | [RWS1909 - Swords 07.jpeg](https://upload.wikimedia.org/wikipedia/commons/2/26/RWS1909_-_Swords_07.jpeg) | 816x1433 | `984b59af83c82ce361bb1ef9a7259b363fdf1c4a` |
| swords08 | [RWS1909 - Swords 08.jpeg](https://upload.wikimedia.org/wikipedia/commons/f/f7/RWS1909_-_Swords_08.jpeg) | 838x1428 | `07d70c55a3095a8f8a6879b87a0af7a6f9a46a4b` |
| swords09 | [RWS1909 - Swords 09.jpeg](https://upload.wikimedia.org/wikipedia/commons/a/a5/RWS1909_-_Swords_09.jpeg) | 817x1453 | `d16c54558b5e8ff01e4a6aae84cac22696d993f4` |
| swords10 | [RWS1909 - Swords 10.jpeg](https://upload.wikimedia.org/wikipedia/commons/e/eb/RWS1909_-_Swords_10.jpeg) | 846x1453 | `7dbccb1863a686e33332a138ba738e7fa61e981a` |
| swords11 | [RWS1909 - Swords 11.jpeg](https://upload.wikimedia.org/wikipedia/commons/0/0b/RWS1909_-_Swords_11.jpeg) | 818x1430 | `768347cafa77e469ea980625a882f0c7deeea15b` |
| swords12 | [RWS1909 - Swords 12.jpeg](https://upload.wikimedia.org/wikipedia/commons/d/d3/RWS1909_-_Swords_12.jpeg) | 837x1419 | `fde92a3d799458dc155e20395a5af2ea97e8aa55` |
| swords13 | [RWS1909 - Swords 13.jpeg](https://upload.wikimedia.org/wikipedia/commons/f/fd/RWS1909_-_Swords_13.jpeg) | 819x1452 | `0264b7ef785ef66b13a05f876d20f59525332a79` |
| swords14 | [RWS1909 - Swords 14.jpeg](https://upload.wikimedia.org/wikipedia/commons/f/f4/RWS1909_-_Swords_14.jpeg) | 846x1442 | `b184acadfdaf8857ccd9648fca15c568ca0d6f01` |
| pentacles01 | [RWS1909 - Pentacles 01.jpeg](https://upload.wikimedia.org/wikipedia/commons/a/a9/RWS1909_-_Pentacles_01.jpeg) | 819x1423 | `0b5ad3db2563633c91137e305e5fc46667eab35c` |
| pentacles02 | [RWS1909 - Pentacles 02.jpeg](https://upload.wikimedia.org/wikipedia/commons/c/c2/RWS1909_-_Pentacles_02.jpeg) | 833x1423 | `4882f1a48e9da24dbc2a5299b8c32b6d91d94c31` |
| pentacles03 | [RWS1909 - Pentacles 03.jpeg](https://upload.wikimedia.org/wikipedia/commons/5/5e/RWS1909_-_Pentacles_03.jpeg) | 817x1443 | `26e96d1971c39671991ed2b05d953f436c6b7429` |
| pentacles04 | [RWS1909 - Pentacles 04.jpeg](https://upload.wikimedia.org/wikipedia/commons/8/8c/RWS1909_-_Pentacles_04.jpeg) | 843x1449 | `28b0bf85a95a88405ce78d3fb505e9f68a77c953` |
| pentacles05 | [RWS1909 - Pentacles 05.jpeg](https://upload.wikimedia.org/wikipedia/commons/7/7d/RWS1909_-_Pentacles_05.jpeg) | 823x1412 | `7085962a7306001c6e2631d70f503ac94593a52f` |
| pentacles06 | [RWS1909 - Pentacles 06.jpeg](https://upload.wikimedia.org/wikipedia/commons/1/1a/RWS1909_-_Pentacles_06.jpeg) | 840x1415 | `81d52cf31f2ff57753cb6b391226fcc290e92058` |
| pentacles07 | [RWS1909 - Pentacles 07.jpeg](https://upload.wikimedia.org/wikipedia/commons/5/51/RWS1909_-_Pentacles_07.jpeg) | 818x1444 | `d8c0dc7c35cd3160d65883c25290465423afc48e` |
| pentacles08 | [RWS1909 - Pentacles 08.jpeg](https://upload.wikimedia.org/wikipedia/commons/e/e6/RWS1909_-_Pentacles_08.jpeg) | 839x1452 | `9984e6f9251f129bca8617cdaccd2807ab2cbe33` |
| pentacles09 | [RWS1909 - Pentacles 09.jpeg](https://upload.wikimedia.org/wikipedia/commons/5/5e/RWS1909_-_Pentacles_09.jpeg) | 817x1420 | `e65e1704f10015f881de007d6a7a52bf8996743a` |
| pentacles10 | [RWS1909 - Pentacles 10.jpeg](https://upload.wikimedia.org/wikipedia/commons/b/bb/RWS1909_-_Pentacles_10.jpeg) | 834x1434 | `6d8eb5fe51bb7c63aecff6b4fc1f928ad3923842` |
| pentacles11 | [RWS1909 - Pentacles 11.jpeg](https://upload.wikimedia.org/wikipedia/commons/8/83/RWS1909_-_Pentacles_11.jpeg) | 816x1438 | `c2d6c49e9bf4b28a8025dc14521756ffb085f230` |
| pentacles12 | [RWS1909 - Pentacles 12.jpeg](https://upload.wikimedia.org/wikipedia/commons/0/03/RWS1909_-_Pentacles_12.jpeg) | 844x1440 | `1e061b55a627f1850fa93662d52a554142f836d4` |
| pentacles13 | [RWS1909 - Pentacles 13.jpeg](https://upload.wikimedia.org/wikipedia/commons/e/e1/RWS1909_-_Pentacles_13.jpeg) | 825x1431 | `15632022e52b8daea2d9376e71685cf83008b834` |
| pentacles14 | [RWS1909 - Pentacles 14.jpeg](https://upload.wikimedia.org/wikipedia/commons/e/ed/RWS1909_-_Pentacles_14.jpeg) | 839x1419 | `981124b4917c0114011de7b53995a5a50de9feb0` |
