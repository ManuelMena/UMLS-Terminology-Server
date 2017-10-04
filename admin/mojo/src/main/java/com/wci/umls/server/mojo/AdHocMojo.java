/**
 * Copyright 2016 West Coast Informatics, LLC
 */
/**
 * Copyright (c) 2012 International Health Terminology Standards Development
 * Organisation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wci.umls.server.mojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.wci.umls.server.jpa.content.ConceptRelationshipJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.model.content.Relationship;
import com.wci.umls.server.services.ContentService;

/**
 * Goal which performs an ad hoc task.
 * 
 * See admin/db/pom.xml for sample usage
 */
@Mojo(name = "ad-hoc", defaultPhase = LifecyclePhase.PACKAGE)
public class AdHocMojo extends AbstractMojo {

  /**
   * Instantiates a {@link AdHocMojo} from the specified parameters.
   */
  public AdHocMojo() {
    // do nothing
  }

  /* see superclass */
  @Override
  public void execute() throws MojoFailureException {

    try {

      getLog().info("Ad Hoc Mojo");

      ContentService service = new ContentServiceJpa();
      service.setMolecularActionFlag(false);
      service.setLastModifiedBy("admin");
      List<Long> relationshipIdList = new ArrayList<>(Arrays.asList(22169L,
          597961L, 619377L, 667915L, 668484L, 671496L, 893580L, 893582L,
          1207522L, 1225496L, 1421696L, 1421966L, 2109914L, 2307574L, 484709L,
          1225691L, 2111078L, 3488547L, 1386493L, 899324L, 62439L, 2357536L,
          674999L, 1834806L, 200L, 3131152L, 316346L, 3385799L, 260423L,
          399152L, 882616L, 1678646L, 1777694L, 1845861L, 2140992L, 603393L,
          2295591L, 598889L, 3131156L, 197610L, 628261L, 619062L, 2084667L,
          222600L, 645235L, 565707L, 1429464L, 424637L, 565100L, 613530L,
          628263L, 2283515L, 619064L, 2251380L, 316752L, 610928L, 612762L,
          630923L, 675001L, 1069151L, 1429466L, 673925L, 1758227L, 2359361L,
          601408L, 601410L, 600221L, 2283517L, 2189993L, 407993L, 15661L,
          295732L, 295785L, 296792L, 469563L, 469663L, 469806L, 676562L,
          1054180L, 1121829L, 1417990L, 1556380L, 1640134L, 2043491L, 424639L,
          424641L, 1071092L, 2167479L, 93249L, 115859L, 1416527L, 2096437L,
          651514L, 484711L, 73079L, 73427L, 75255L, 75270L, 1411537L, 2257798L,
          73025L, 73027L, 571062L, 73030L, 73032L, 78949L, 78889L, 3385801L,
          1125733L, 603665L, 2178527L, 2183446L, 2285337L, 524983L, 541355L,
          66542L, 2141062L, 134136L, 619066L, 1389784L, 1391296L, 1851742L,
          2085054L, 2085062L, 2298393L, 668059L, 2297248L, 2297263L, 2101711L,
          69653L, 2846444L, 634888L, 1845676L, 517166L, 2118652L, 668072L,
          94786L, 492925L, 1158721L, 1199623L, 757560L, 2445528L, 2445538L,
          442302L, 2105182L, 609214L, 712224L, 1779564L, 2103632L, 1201377L,
          1201379L, 2120013L, 1307968L, 2102901L, 727766L, 1371116L, 2387856L,
          634432L, 1199920L, 610777L, 675468L, 2107434L, 2101774L, 31925L,
          324339L, 612764L, 1069153L, 2102410L, 679726L, 667163L, 2392387L,
          289795L, 665867L, 1390162L, 1391462L, 3385803L, 259830L, 484735L,
          1095633L, 37070L, 285865L, 1225652L, 306020L, 1848418L, 1848420L,
          3948056L, 1052265L, 342431L, 604063L, 437445L, 728290L, 1322035L,
          426928L, 2090919L, 215025L, 259849L, 3593756L, 259832L, 2845005L,
          1972945L, 289697L, 25970L, 564920L, 1326244L, 2111853L, 2373818L,
          1779473L, 316765L, 581809L, 711193L, 1037495L, 2121717L, 1201637L,
          2024425L, 728422L, 563535L, 1178819L, 1179064L, 1179298L, 1179321L,
          1193203L, 1193218L, 1202719L, 2846959L, 222602L, 260381L, 213049L,
          2175264L, 62430L, 62441L, 1565578L, 62432L, 377208L, 599460L,
          3270882L, 675924L, 647105L, 616605L, 225729L, 1204055L, 634436L,
          22768L, 3283803L, 3154624L, 42332L, 274363L, 332972L, 361522L,
          482784L, 483206L, 633400L, 1187085L, 1392386L, 1419762L, 2057811L,
          2111819L, 3106073L, 620012L, 2121241L, 2302404L, 197617L, 1205262L,
          747647L, 1379496L, 1756021L, 2086734L, 2086743L, 2288340L, 316767L,
          2094992L, 428235L, 234293L, 3102728L, 345114L, 344622L, 651081L,
          1849224L, 1559952L, 467068L, 1225565L, 637398L, 629444L, 352396L,
          352376L, 665951L, 358397L, 356870L, 1656974L, 316754L, 1300619L,
          1225320L, 1200439L, 1078387L, 691896L, 2096846L, 1373598L, 2103074L,
          860163L, 297101L, 478346L, 502045L, 1390507L, 1390510L, 1390513L,
          1390516L, 1391610L, 1851905L, 1853206L, 1375904L, 2045447L, 1373631L,
          383165L, 383153L, 484737L, 484739L, 1375304L, 25973L, 2047271L,
          2691701L, 680294L, 484705L, 650614L, 1037073L, 1427499L, 2099476L,
          62401L, 513553L, 1069155L, 1071094L, 2043872L, 2257793L, 634434L,
          899315L, 484741L, 3491485L, 898948L, 1225573L, 2044031L, 2387846L,
          1087820L, 39205L, 63228L, 63274L, 559252L, 559674L, 860307L, 1390724L,
          1390729L, 1391706L, 1518201L, 1851970L, 1851975L, 1851979L, 2087806L,
          2087814L, 2304768L, 2304784L, 2387910L, 565102L, 736238L, 601412L,
          254837L, 2140443L, 340195L, 430270L, 430256L, 882611L, 673327L,
          1225650L, 237451L, 585691L, 1779642L, 893731L, 160907L, 1375191L,
          728090L, 484707L, 622335L, 618114L, 674474L, 622337L, 2253232L,
          2200819L, 592989L, 610780L, 878513L, 345582L, 62443L, 62434L, 62445L,
          377210L, 1205264L, 502871L, 602874L, 616584L, 659003L, 665225L,
          665760L, 666522L, 666524L, 1178906L, 316769L, 316756L, 674545L,
          896745L, 9471L, 72201L, 218938L, 387575L, 387676L, 405208L, 412789L,
          452082L, 484848L, 484867L, 484897L, 484936L, 525598L, 596087L,
          884489L, 1034360L, 1034437L, 1054057L, 1539917L, 1601526L, 1847903L,
          2041055L, 2104261L, 2107192L, 484713L, 484715L, 484717L, 484719L,
          3385805L, 134601L, 704621L, 2024427L, 628723L, 628715L, 377212L,
          680299L, 480862L, 1326251L, 666800L, 407984L, 739393L, 130025L,
          728501L, 3155342L, 3272130L, 893733L, 1022654L, 2043254L, 738847L,
          88103L, 2334020L, 484721L, 667852L, 91351L, 1010109L, 2173960L,
          628266L, 1206885L, 664428L, 1327688L, 672246L, 1419947L, 638130L,
          552271L, 552212L, 2090780L, 424643L, 424645L, 283857L, 1069157L,
          1222158L, 3948090L, 260444L, 39210L, 424697L, 1391149L, 1391898L,
          2307788L, 2107377L, 1062074L, 37417L, 1385293L, 1385296L, 1385299L,
          1563114L, 1749698L, 2088939L, 2251383L, 74058L, 1206887L, 1391167L,
          1391173L, 1391908L, 3272132L, 1855309L, 279567L, 437448L, 1062152L,
          3560718L, 2824530L, 2140505L, 465279L, 3191665L, 3191673L, 484723L,
          205L, 29607L, 297649L, 2090036L, 2090042L, 2091938L, 2282552L,
          2358951L, 52686L, 629627L, 2113541L, 48257L, 48296L, 425433L,
          3203370L, 637400L, 481573L, 673329L, 1384886L, 1384888L, 1384890L,
          1563119L, 1749596L, 1753719L, 2085087L, 2085094L, 3746374L, 27003L,
          1758566L, 3948092L, 85981L, 1377946L, 1841533L, 234403L, 2175372L,
          3948094L, 674369L, 3948096L, 161696L, 672781L, 3806865L, 672084L,
          1192674L, 180031L, 466069L, 1846245L, 42352L, 3117391L, 612942L,
          42355L, 198722L, 1206983L, 1384843L, 1384845L, 1384847L, 1563127L,
          1749580L, 611894L, 2300203L, 3948098L, 3948100L, 39215L, 1755997L,
          1052269L, 2359560L, 481575L, 305412L, 2291250L, 461397L, 2175751L,
          2113872L, 33811L, 41286L, 96563L, 2086868L, 2086876L, 2086884L,
          2086892L, 2302917L, 2302923L, 2309044L, 2309050L, 207L, 2102469L,
          321891L, 2284869L, 673715L, 460730L, 461685L, 1375307L, 2110135L,
          2304331L, 2309188L, 2140535L, 627571L, 2121205L, 2175934L, 2251398L,
          2246022L, 2246031L, 2141073L, 2156125L, 622946L, 31939L, 39219L,
          546259L, 1065141L, 1065216L, 496695L, 497042L, 1376436L, 2088363L,
          2306501L, 2306507L, 2045034L, 352246L, 600226L, 2375737L, 2257683L,
          888740L, 42359L, 1202754L, 2174694L, 675926L, 2049255L, 3948062L,
          3948102L, 2375671L, 316771L, 175685L, 314466L, 411694L, 634483L,
          634438L, 127788L, 1176630L, 1187055L, 1193819L, 1225646L, 348554L,
          601542L, 1135018L, 1214034L, 551281L, 1199302L, 1071272L, 2025112L,
          3948104L, 645332L, 37227L, 642720L, 2176935L, 302616L, 2248422L,
          405235L, 345116L, 72075L, 1849328L, 481593L, 2309873L, 3548434L,
          2696275L, 2873323L, 548337L, 3948106L, 3948108L, 481578L, 3948064L,
          481580L, 2283529L, 213052L, 352404L, 481582L, 481584L, 511921L,
          2283531L, 3948066L, 206530L, 2091157L, 532168L, 860236L, 1390539L,
          1391620L, 1759737L, 3948110L, 210L, 98659L, 132106L, 3948112L,
          3948114L, 212L, 2256435L, 3948116L, 3948118L, 3948120L, 3948068L,
          3948070L, 3948072L, 3948074L, 3948076L, 3948122L, 3948124L, 3948126L,
          2283533L, 214L, 1225547L, 610430L, 3948078L, 551189L, 609271L,
          2118175L, 436051L, 602959L, 621994L, 43152L, 737455L, 606850L,
          2118177L, 461529L, 3154455L, 483210L, 22035L, 42369L, 737068L,
          895322L, 1069130L, 1810778L, 1385254L, 1385256L, 1385258L, 1391068L,
          1391854L, 1563141L, 1749688L, 1753792L, 180062L, 301200L, 631223L,
          2176143L, 62447L, 683019L, 201066L, 404706L, 502739L, 2024429L,
          3385807L, 678877L, 1839567L, 370305L, 2059601L, 494132L, 733412L,
          279573L, 164302L, 2696850L, 1111959L, 1331719L, 170862L, 1200443L,
          736241L, 444307L, 1222034L, 2020842L, 247060L, 283794L, 517567L,
          708584L, 1810753L, 3948058L, 424704L, 727977L, 2087148L, 3948128L,
          675049L, 674003L, 522834L, 513606L, 325200L, 154999L, 1450660L,
          1752051L, 1752054L, 1757702L, 374115L, 667858L, 424654L, 466310L,
          1225656L, 25981L, 433467L, 1210000L, 1353057L, 484725L, 3102918L,
          630491L, 1406912L, 1406924L, 1406938L, 1406943L, 1759344L, 1299718L,
          217L, 219L, 438145L, 518285L, 1201384L, 2112537L, 3948130L, 1409213L,
          1217142L, 675051L, 2829232L, 483218L, 1779612L, 2295267L, 1205453L,
          415693L, 11268L, 412558L, 2121084L, 2508024L, 2260054L, 2443072L,
          541705L, 2554003L, 518947L, 484727L, 484729L, 1268959L, 405239L,
          279575L, 2047264L, 233942L, 616429L, 1518203L, 484743L, 62449L,
          2054607L, 565462L, 586863L, 2251385L, 2283522L, 628268L, 628270L,
          2283524L, 3948132L, 1374276L, 675053L, 42373L, 198731L, 409438L,
          563585L, 63612L, 409443L, 639204L, 3948080L, 369917L, 421109L,
          220389L, 2100102L, 712657L, 62436L, 84346L, 637402L, 2259519L,
          137553L, 2221024L, 634927L, 283863L, 481595L, 283865L, 283867L,
          283869L, 634929L, 316774L, 610493L, 283871L, 283873L, 634931L,
          2257685L, 638946L, 149839L, 2298666L, 2298672L, 2085561L, 2091205L,
          2298744L, 178441L, 3126812L, 368299L, 727791L, 2387943L, 2178512L,
          1777701L, 165214L, 165295L, 894038L, 279927L, 283845L, 630925L,
          306024L, 2044078L, 3724895L, 324916L, 478584L, 1205288L, 2091920L,
          1205266L, 898682L, 546264L, 580686L, 2309892L, 612770L, 221L, 882613L,
          637394L, 894672L, 728101L, 2387980L, 564573L, 363269L, 223L, 671805L,
          345608L, 419359L, 634905L, 437220L, 222607L, 881332L, 9970L, 1253294L,
          1253289L, 1036581L, 2393516L, 2059610L, 1299721L, 892282L, 1299528L,
          3094814L, 361722L, 170176L, 2712227L, 253122L, 262155L, 505475L,
          550991L, 716244L, 884376L, 3282224L, 1880064L, 174058L, 1845767L,
          373919L, 2114359L, 381597L, 1068411L, 2251543L, 444199L, 393819L,
          622343L, 378205L, 628727L, 603671L, 325204L, 612811L, 612813L,
          612815L, 603163L, 603165L, 603167L, 675097L, 675099L, 675101L,
          565711L, 565713L, 565715L, 10706L, 3701580L, 98627L, 213054L, 377222L,
          377224L, 377226L, 377228L, 667860L, 424656L, 424658L, 675103L,
          2283539L, 565107L, 580761L, 580764L, 98629L, 213056L, 377230L,
          667862L, 424660L, 675105L, 2283541L, 565109L, 580766L, 316776L,
          890772L, 890774L, 890776L, 890778L, 894652L, 1644557L, 1758296L,
          1758301L, 1758306L, 1758371L, 1758376L, 1758380L, 73043L, 71531L,
          2928833L, 62451L, 2259521L, 316778L, 551202L, 2057627L, 2105357L,
          225L, 227L, 2334779L, 2718882L, 3943426L, 1422926L, 2696456L,
          1422919L, 2193531L, 405242L, 38636L, 42375L, 1450663L, 793459L,
          1450633L, 424662L, 1052271L, 2102784L, 484731L, 62453L, 345310L,
          565717L, 603169L, 612817L, 675107L, 296575L, 3307542L, 484745L,
          62455L, 3948082L, 1409217L, 358388L, 25983L, 2513711L, 3735193L,
          612819L, 603171L, 675110L, 565719L, 853478L, 853480L, 603173L,
          675112L, 613533L, 325208L, 853482L, 46999L, 1409219L, 1409221L,
          1409223L, 1409225L, 1409227L, 1409229L, 603420L, 890781L, 667854L,
          25985L, 1201091L, 266181L, 3159785L, 164701L, 896749L, 437453L,
          3690816L, 733495L, 675056L, 2345575L, 22049L, 2843863L, 684327L,
          603677L, 2084489L, 2084494L, 2084500L, 2091647L, 2182200L, 2184435L,
          2295235L, 2308342L, 3177244L, 2090000L, 2091056L, 2296818L, 2187264L,
          3171834L, 128988L, 3102193L, 1373515L, 3536297L, 25979L, 610783L,
          2305900L, 2375150L, 484747L, 227241L, 227314L, 345120L, 651512L,
          98631L, 377232L, 424664L, 424666L, 424668L, 2105413L, 377234L,
          580899L, 1368831L, 260352L, 728103L, 283525L, 495799L, 680508L,
          641341L, 484733L, 519696L, 62458L, 409752L, 2101945L, 419371L,
          1204495L, 2088135L, 2088141L, 2088148L, 2088154L, 2305848L, 2305855L,
          2309325L, 2375360L, 629223L, 378945L, 2103180L, 2303550L, 3948134L,
          399476L, 1051568L, 3948084L, 3948086L, 3948088L, 2177711L, 631546L,
          2359083L, 2059605L, 2928835L, 1062060L, 1421054L, 316758L, 698335L,
          1297981L, 2053046L, 2178619L, 2178630L, 2178637L, 2305080L, 1842615L,
          1842617L, 1842619L, 35810L, 3692341L, 98633L, 98635L, 603175L,
          603177L, 2102787L, 2102790L, 1199767L, 2102229L, 2102231L, 2102912L,
          325211L, 325214L, 619084L, 619086L, 619088L, 619090L, 736243L,
          2251387L, 424674L, 424676L, 2044171L, 2044173L, 2044175L, 2044177L,
          2103327L, 2103329L, 628729L, 2106300L, 2106302L, 565721L, 1845344L,
          599465L, 599467L, 552414L, 255627L, 1845347L, 667170L, 1199769L,
          2103592L, 2103594L, 1842621L, 1205268L, 599469L, 2139492L, 2304471L,
          2387964L, 334466L, 71548L, 370533L, 405245L, 1100613L, 2102792L,
          2102795L, 102065L, 2297387L, 2297400L, 3154710L, 192480L, 2043900L,
          2086314L, 2086322L, 2108387L, 200899L, 2299235L, 619761L, 1518207L,
          2085148L, 2085165L, 2101524L, 2101537L, 170300L, 2086528L, 2246410L,
          374061L, 2045449L, 2088233L, 2088240L, 2387864L, 2091253L, 2091255L,
          165032L, 484750L, 2259523L, 160981L, 1421056L, 2176938L, 1852830L,
          2304082L, 2304088L, 2088760L, 2088766L, 2246149L, 2307693L, 484752L,
          565397L, 187881L, 2140594L, 2102372L, 2176344L, 484L, 622340L,
          3695194L, 9993L, 316760L, 263988L, 894043L, 600550L, 618821L,
          1373606L, 672936L, 674378L, 131587L, 165303L, 3153631L, 899330L,
          623108L, 3893426L, 322072L, 3487951L, 279570L, 2094655L, 427025L,
          591555L, 2299944L, 2299960L, 622660L, 2107572L, 26306L, 93252L,
          626054L, 627224L, 2309362L, 3948060L, 63729L, 544569L, 630998L,
          291152L, 3948389L, 604646L, 618764L, 623836L, 675986L, 3350122L,
          2108822L, 645562L, 2105363L, 2048503L, 1200591L, 86861L, 2066649L,
          2066651L, 2066653L, 3892287L, 1842625L, 86863L, 1842628L, 1845661L,
          61182L, 1423302L, 3103570L, 464567L, 1173842L, 2245995L, 2245572L,
          625967L, 625970L, 2106305L, 2102918L, 649478L, 41302L, 568512L,
          1064863L, 2087389L, 623838L, 1375031L, 2303576L, 2303588L, 464135L,
          668972L, 630200L, 1196878L, 409841L, 73048L, 1138634L, 1418000L,
          2105022L, 985122L, 2447176L, 2447157L, 599463L, 39236L, 59969L,
          665863L, 666989L, 670175L, 1065123L, 1065729L, 1391087L, 1391870L,
          3385809L, 621145L, 87989L, 3891791L, 329172L, 3268276L, 617830L,
          1842634L, 896752L, 27067L, 1845361L, 101853L, 101856L, 2101714L,
          2101716L, 98647L, 1199627L, 1199629L, 1199771L, 2102413L, 2140509L,
          2140511L, 612945L, 323318L, 3982469L, 619094L, 619096L, 2045451L,
          2251558L, 2251561L, 3948136L, 2105416L, 2105418L, 622345L, 2094661L,
          424681L, 424684L, 2066655L, 3695208L, 2044181L, 2044183L, 1846376L,
          628733L, 628735L, 489L, 2106307L, 565113L, 1842636L, 619098L, 619100L,
          622347L, 2044185L, 2156138L, 660722L, 1206902L, 3746081L, 3724986L,
          525020L, 1422193L, 2837359L, 1810785L, 2348613L, 2346993L, 16017L,
          2719060L, 600134L, 2049790L, 48020L, 616451L, 3422784L, 2373820L,
          264939L, 2373695L, 1846505L, 3949339L, 2044189L, 633065L, 630089L,
          2377891L, 2377897L, 2377903L, 2377826L, 2377828L, 2377830L, 419369L,
          174054L, 2103325L, 424648L, 1200538L, 2094663L, 1222032L, 212472L,
          1268970L, 2510707L, 998137L, 159515L, 159556L, 2264634L, 2264572L,
          975481L, 2443066L, 1708201L, 1010106L, 3283441L, 399577L, 3114690L,
          660793L, 1422921L, 712229L, 3191619L, 1313159L, 3691063L, 1422195L,
          2358601L, 590808L, 895354L, 2337424L, 1838508L, 260094L, 126591L,
          285460L, 3701584L, 2871662L, 2871651L, 662814L, 3114559L, 1416683L,
          2053060L, 1299716L, 1845750L, 343194L, 887800L, 3170082L, 2194024L,
          316762L, 2898338L, 2695929L, 610930L, 1200287L, 3927481L, 22328L,
          31306L, 2120291L, 674479L, 316469L, 2101718L, 517574L, 3548712L,
          3696030L, 1779557L, 3102926L, 1845663L, 3178349L, 1842639L, 3176894L,
          2703213L, 593005L, 593007L, 601501L, 2288346L, 3625350L, 298708L,
          3291913L, 517806L, 580772L, 1364240L, 2618784L, 316366L, 3270880L,
          1584232L, 2176147L, 23359L, 84300L, 214953L, 485433L, 681537L,
          2283626L, 2360597L, 3548793L, 2121670L, 10276L, 413105L, 3734882L,
          1845776L, 660724L, 3157476L, 3435693L, 588783L, 3949512L, 259837L,
          3268863L, 1779648L, 2713184L, 3860958L, 2084663L, 2110495L, 2305641L,
          3157748L, 1387661L, 2851318L, 1205194L, 2331722L, 3515029L, 1748771L,
          2320906L, 603161L, 610319L, 3691888L, 2285862L, 2180257L, 3927838L,
          2121208L, 3130772L, 3892546L, 1422197L, 231658L, 564917L, 603511L,
          606101L, 607960L, 613072L, 613352L, 632257L, 632275L, 641947L,
          665212L, 665217L, 665233L, 667006L, 667908L, 668147L, 668443L,
          669000L, 669105L, 669113L, 669231L, 669235L, 669238L, 669241L,
          669244L, 669247L, 669541L, 669544L, 672138L, 734451L, 736758L,
          894086L, 1068100L, 1072043L, 1643216L, 2045854L, 2047371L, 2047401L,
          2047404L, 2156195L, 2304031L, 2175270L, 2375169L, 3587815L, 2302667L,
          551962L, 551963L, 316449L, 316450L, 619140L, 619141L, 1758563L,
          1758564L, 610426L, 610427L, 671341L, 671342L, 1838292L, 1838293L,
          1831601L, 1831602L, 1831603L, 2106174L, 2106175L, 2139203L, 2139204L,
          2105331L, 2105332L, 894032L, 894033L, 894034L, 894035L, 894036L,
          3102189L, 3102190L, 3102191L, 611112L, 611113L, 611114L, 611115L,
          611116L, 2375729L, 2375730L, 2375731L, 2118654L, 2118655L, 2175429L,
          2175430L, 22770L, 22771L, 614699L, 614700L, 2375733L, 2375734L,
          2375735L, 619143L, 619144L, 407997L, 407998L, 2116832L, 2116833L,
          371288L, 371289L, 908263L, 908264L, 622332L, 622333L, 430665L,
          430666L, 430601L, 430602L, 1262237L, 1262238L, 1262239L, 728086L,
          728087L, 2085578L, 2085579L, 2085586L, 2085587L, 2085594L, 2085595L,
          2085603L, 2085604L, 2085611L, 2085612L, 2085620L, 2085621L, 2102062L,
          2102063L, 1831606L, 1831607L, 1831608L, 1831609L, 603668L, 603669L,
          1846415L, 1846416L, 2156122L, 2156123L, 1200803L, 1200804L, 565257L,
          565258L, 480L, 481L, 3487948L, 3487949L, 565261L, 565262L, 532785L,
          532786L, 565198L, 565199L, 2102168L, 2102169L, 465288L, 465289L,
          29586L, 29587L, 208763L, 208764L, 208765L, 208766L, 208767L, 324909L,
          324910L, 2140664L, 2140665L, 23549L, 23550L, 363132L, 363133L,
          4028571L, 4028572L, 4028573L, 408610L, 408611L, 895783L, 895784L,
          671344L, 671345L, 36981L, 36982L, 665858L, 665859L, 1225740L,
          1225741L, 1225743L, 1225744L, 1192987L, 1192988L, 442306L, 442307L,
          165222L, 165223L, 165224L, 165225L, 165226L, 637107L, 637108L,
          408001L, 408002L, 2091847L, 2091848L, 671453L, 671454L, 468450L,
          468451L, 671349L, 671350L, 671352L, 671353L, 430668L, 430669L,
          430670L, 2140130L, 2140131L, 27024L, 27025L, 43537L, 43538L, 43539L,
          461491L, 461492L, 461493L, 461494L, 36985L, 36986L, 2037083L,
          2037084L, 3694866L, 3694867L, 467842L, 467843L, 1838295L, 1838296L,
          442309L, 442310L, 442312L, 442313L, 442315L, 442316L, 442318L,
          442319L, 442321L, 442322L, 442324L, 442325L, 2102319L, 2102320L,
          2102321L, 1069159L, 1069160L, 453302L, 453303L, 2179333L, 2179334L,
          2179335L, 2305450L, 2305451L, 3625129L, 3625130L, 3625131L, 584689L,
          584690L, 2087775L, 2087776L, 2087777L, 2284670L, 2284671L, 2284672L,
          2304637L, 2304638L, 2304639L, 161832L, 161833L, 102319L, 102320L,
          2182023L, 2182024L, 2297588L, 2297589L, 377031L, 377032L, 264310L,
          264311L, 127942L, 127943L, 1420242L, 1420243L, 618477L, 618478L,
          468312L, 468313L, 313449L, 313450L, 2102066L, 2102067L, 2102068L,
          2106182L, 2106183L, 2285603L, 2285604L, 2102325L, 2102326L, 2102327L,
          2282645L, 2282646L, 2106189L, 2106190L, 2102330L, 2102331L, 2102332L,
          2102070L, 2102071L, 212468L, 212469L, 212470L, 345601L, 345602L,
          345603L, 3745021L, 3745022L, 178331L, 178332L, 178333L, 559206L,
          559207L, 2102074L, 2102075L, 2102076L, 1845754L, 1845755L, 2965585L,
          2965586L, 619978L, 619979L, 619980L, 739392L, 1770044L, 1770045L,
          174046L, 2105609L, 2353811L, 1555641L, 2256806L, 2256808L, 2256810L,
          345482L, 345484L, 3927588L, 501936L, 2105570L, 2105571L, 2106395L,
          405225L, 2711360L, 1226334L, 1376379L, 2105519L, 2353816L, 2353817L,
          1221773L, 2106218L, 2105243L, 3982089L, 665228L, 668074L, 2104963L,
          2190023L, 2109863L, 20916L, 1111952L, 2108960L, 2106423L, 1224476L,
          2284292L, 2284293L, 2066991L, 3283860L, 2104973L, 1326243L, 1114544L,
          2066992L, 2066993L, 2066994L, 616286L, 616287L, 2200965L, 1047579L,
          4124093L, 2934917L, 1326246L, 2190134L, 2190135L, 3746903L, 2302115L,
          2110566L, 3284019L, 1225881L, 2106437L, 2106447L, 2106457L, 2066996L,
          381784L, 2066997L, 3591049L, 1050400L, 345300L, 1559942L, 1768450L,
          1768452L, 344613L, 47202L, 47204L, 1759628L, 595403L, 1862231L,
          2109429L, 2303478L, 378942L, 378917L, 2049332L, 2084568L, 328730L,
          1022511L, 61695L, 2104064L, 2100030L, 2201015L, 4092781L, 1862232L,
          3104712L, 898967L, 898971L, 1069469L, 2537786L, 626105L, 628467L,
          628714L, 56577L, 2141278L, 2141283L, 2104427L, 2104434L, 674547L,
          2106337L, 2106341L, 2375371L, 3154785L, 3746592L, 645919L, 2104839L,
          2100888L, 2104488L, 2104495L, 3547328L, 2107352L, 2107342L, 2190357L,
          578513L, 578438L, 2104428L, 3578532L, 3593702L, 3117662L, 2712860L,
          356867L, 2291111L, 3102765L, 2248473L, 2139207L, 2066998L, 675908L,
          291067L, 291069L, 2179565L, 2102419L, 2102420L, 2155945L, 2105302L,
          1672343L, 2105150L, 2110029L, 2305789L, 3757776L, 481577L, 495468L,
          496854L, 3158257L, 1834554L, 1834555L, 2297178L, 2021430L, 541184L,
          660033L, 2309829L, 647110L, 1776756L, 127787L, 132125L, 3158637L,
          518349L, 614829L, 1556790L, 2597185L, 3094451L, 1416627L, 2697178L,
          2171236L, 2106556L, 2306300L, 11515L, 2107092L, 2317371L, 1125731L,
          2141335L, 2248114L, 890762L, 888241L, 436159L, 436160L, 1117160L,
          2398507L, 2442907L, 2554670L, 3101773L, 387856L, 299465L, 3595537L,
          342115L, 2141365L, 468286L, 2105578L, 2105579L, 2714089L, 1078381L,
          1077216L, 2691778L, 2042543L, 184294L, 275035L, 899317L, 824241L,
          2221172L, 1198962L, 1139901L, 1375481L, 2179216L, 101838L, 2092121L,
          2178968L, 211788L, 2295845L, 2066647L, 323304L, 87835L, 262044L,
          312302L, 1845764L, 2251540L, 1199764L, 90213L, 704236L, 2889660L,
          2717727L, 3117640L, 34953L, 688132L, 345107L, 619070L, 1880060L,
          352398L, 345109L, 345110L, 14717L, 14720L, 662394L, 633062L, 633063L,
          2140531L, 2140533L, 2090333L, 2303671L, 2305629L, 2305634L, 2140679L,
          2179913L, 1373510L, 2299879L, 2299886L, 2300067L, 2300074L, 2300081L,
          3949430L, 2300170L, 2928556L, 366517L, 412181L, 1692516L, 3270031L,
          640956L, 2105212L, 2120283L, 2120284L, 1100609L, 3132269L, 2177841L,
          2283526L, 2308765L, 2891393L, 2891395L, 378973L, 2118767L, 2139893L,
          2091408L, 1225539L, 225209L, 276584L, 276650L, 276664L, 323323L,
          335819L, 612352L, 380956L, 2101815L, 2101816L, 2101817L, 1841951L,
          2101776L, 2061122L, 1223235L, 2105457L, 3949755L, 407987L, 546859L,
          2091250L, 2087006L, 2087013L, 2087020L, 2105545L, 2104501L, 2187334L,
          617408L, 617409L, 405227L, 517794L, 580755L, 517795L, 552410L,
          552411L, 2102246L, 3592964L, 542952L, 148952L, 255626L, 619075L,
          2021763L, 119377L, 618213L, 2105411L, 2105380L, 2094775L, 90214L,
          2101947L, 56576L, 56578L, 1070170L, 1070171L, 21630L, 101839L,
          524985L, 524986L, 56579L, 197613L, 323305L, 323306L, 323307L, 736240L,
          739395L, 565709L, 565104L, 3695190L, 186761L, 378181L, 171112L,
          619700L, 3695192L, 322061L, 2060781L, 2022938L, 2022941L, 607129L,
          2060757L, 1841915L, 1841916L, 3176890L, 1842501L, 517565L, 517566L,
          882615L, 1069129L, 617410L, 734552L, 2246141L, 2307181L, 2307195L,
          2307201L, 2307208L, 2307216L, 2295729L, 2406360L, 2044169L, 1223237L,
          1201321L, 617340L, 1842517L, 2102352L, 2337261L, 151990L, 314960L,
          314976L, 570038L, 296571L, 409560L, 1125732L, 3104680L, 2174697L,
          885389L, 606972L, 1375017L, 2303600L, 2303472L, 2303474L, 2933606L,
          46996L, 46997L, 46998L, 2044358L, 212581L, 212582L, 601711L, 2174920L,
          1224940L, 634910L, 1845785L, 1845786L, 1845808L, 1845809L, 1845810L,
          1845826L, 316459L, 2251578L, 2251579L, 378182L, 2251541L, 1841952L,
          1842000L, 1842001L, 3695210L, 3695211L, 3695212L, 625959L, 737458L,
          2174698L, 2174699L, 2174700L, 2174701L, 2174702L, 2046695L, 659002L,
          3695213L, 3695215L, 756456L, 2189951L, 3488418L, 27266L, 98774L,
          98775L, 3154566L, 529314L, 948526L, 2174930L, 948527L, 3100697L,
          474482L, 1011910L, 691898L, 1095631L, 712221L, 64303L, 594785L,
          1070541L, 1459747L, 1422118L, 2047267L, 2047269L, 3545519L, 2933607L,
          1846161L, 2254883L, 2923873L, 2933612L, 2933608L, 306019L, 703113L,
          2500072L, 1018385L, 606887L, 2236226L, 433024L, 1485026L, 592665L,
          2042851L, 2372410L, 529315L, 628722L, 670582L, 2140534L, 3536312L,
          2015331L, 233895L, 322380L, 2345842L, 3263416L, 3541179L, 3537711L,
          2922739L, 554465L, 587285L, 340556L, 2104515L, 589381L, 1048674L,
          2108307L, 2110489L, 2305650L, 2305667L, 2305678L, 2310389L, 2314390L,
          3730404L, 3730405L, 3725008L, 3725009L, 532880L, 314961L, 625960L,
          50706L, 1845811L, 2096374L, 120612L, 411601L, 302905L, 2140641L,
          2140642L, 2140643L, 2022671L, 2022673L, 2105097L, 2105100L, 2105101L,
          2105124L, 2105127L, 2105128L, 2105319L, 2105320L, 2105321L, 2105325L,
          2105326L, 2105327L, 208855L, 208856L, 208869L, 208870L, 208871L,
          208872L, 208873L, 208719L, 208720L, 208757L, 208758L, 208759L,
          208760L, 208761L, 2094951L, 2094952L, 2094953L, 893729L, 893730L,
          2177911L, 2177913L, 2177914L, 2304977L, 2304979L, 2304980L, 2388004L,
          2388005L, 2104435L, 2104436L, 2257640L, 2257641L, 2257642L, 1206908L,
          1206909L, 2387935L, 2387936L, 2387988L, 2387989L, 731412L, 731413L,
          647108L, 647117L, 414499L, 414500L, 2060766L, 2060767L, 1810771L,
          1810773L, 3948318L, 3948320L, 619068L, 619069L, 1199744L, 1199747L,
          2085376L, 2085379L, 23267L, 23277L, 2171504L, 2171507L, 893735L,
          893736L, 1845678L, 1845680L, 299752L, 299757L, 299758L, 580756L,
          580757L, 129948L, 129971L, 129972L, 129949L, 129974L, 129975L,
          161792L, 161826L, 161827L, 161793L, 161829L, 161830L, 2156127L,
          2156132L, 2156128L, 2156134L, 2176282L, 2176284L, 2106192L, 2106193L,
          2297568L, 2297570L, 2297579L, 2297580L, 2106177L, 2106180L, 2156129L,
          2156136L, 483L, 486L, 487L, 2105423L, 2105426L, 2105430L, 2105433L,
          2109624L, 2109626L, 2047266L, 2047268L, 2105659L, 2105660L, 430661L,
          430674L, 430675L, 613326L, 613327L, 613328L, 2106178L, 2106185L,
          2106179L, 2106187L, 430662L, 430680L, 430681L, 2377822L, 2377824L,
          2376860L, 2376861L, 619078L, 619079L, 619080L, 619103L, 442304L,
          442305L, 1199745L, 1199750L));

      // List<Long> relationshipIdList = new ArrayList<>(Arrays.asList(100L));

      for (Long relationshipId : relationshipIdList) {
        Relationship<?, ?> rel = service.getRelationship(relationshipId,
            ConceptRelationshipJpa.class);
        if (rel != null) {
          service.removeRelationship(relationshipId,
              ConceptRelationshipJpa.class);
        }
      }

      getLog().info("done ...");
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }

}
