package com.group4.lumos_api.place;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.group4.lumos_api.place.entity.Place;
import com.group4.lumos_api.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlaceDataInitializer implements CommandLineRunner {

    private final PlaceRepository placeRepository;

    @Override
    public void run(String... args) {
        // 카페
        Place p1 = new Place();
        p1.setName("블루포트 공학관점");
        p1.setBuilding("공학관,공대");
        p1.setType("카페");
        p1.setTime("09:00 - 19:00");
        p1.setLat(35.85910891900613);
        p1.setLng(128.48747324155707);

        Place p2 = new Place();
        p2.setName("카페사월");
        p2.setBuilding("행소박물관");
        p2.setType("카페");
        p2.setTime("09:00 - 18:00");
        p2.setLat(35.85672754000742);
        p2.setLng(128.4898419543818);

        Place p3 = new Place();
        p3.setName("카페ING 동산도서관점");
        p3.setBuilding("도서관");
        p3.setType("카페");
        p3.setTime("08:40 - 18:00");
        p3.setLat(35.85639335738497);
        p3.setLng(128.48772138401628);

        Place p4 = new Place();
        p4.setName("피피커피");
        p4.setBuilding("의양관");
        p4.setType("카페");
        p4.setTime("08:30 - 19:00");
        p4.setLat(35.85625408418409);
        p4.setLng(128.48513954036963);

        Place p5 = new Place();
        p5.setName("붐카페&코너베이커리");
        p5.setBuilding("구바우어관");
        p5.setType("카페");
        p5.setTime("08:00 - 20:00");
        p5.setLat(35.85423022055133);
        p5.setLng(128.4861118722504);

        Place p6 = new Place();
        p6.setName("카페ING 동영관점");
        p6.setBuilding("동영관");
        p6.setType("카페");
        p6.setTime("08:30 - 16:15");
        p6.setLat(35.85320516540829);
        p6.setLng(128.48428844646253);

        Place p7 = new Place();
        p7.setName("이디야커피 계명대명교생활관점");
        p7.setBuilding("명교생활관,기숙사");
        p7.setType("카페");
        p7.setTime("07:50 - 21:00");
        p7.setLat(35.85705832685428);
        p7.setLng(128.4802146357763);

        // 서점
        Place p8 = new Place();
        p8.setName("계명대 구내서점");
        p8.setBuilding("구바우어관");
        p8.setType("서점");
        p8.setTime("09:00 - 19:00");
        p8.setLat(35.85423022055133);
        p8.setLng(128.4861118722504);
        
        //도서관
        Place p9 = new Place();
        p9.setName("동산도서관");
        p9.setBuilding("도서관");
        p9.setType("도서관");
        p9.setTime("09:00 - 19:00");
        p9.setLat(35.85640495369977);
        p9.setLng(128.48714874254276);

        Place p10 = new Place();
        p10.setName("의학도서관");
        p10.setBuilding("의대");
        p10.setType("도서관");
        p10.setTime("08:30 - 22:00");
        p10.setLat(35.85509916572842);
        p10.setLng(128.4805102964029);


        Place p11 = new Place();
        p11.setName("동산도서관 3F");
        p11.setBuilding("도서관");
        p11.setType("프린트");
        p11.setTime("09:00 - 19:00");
        p11.setLat(35.85640495369977);
        p11.setLng(128.48714874254276);

        // 프린트
        Place p12 = new Place();
        p12.setName("공대 1호관 2F");
        p12.setBuilding("공학관,공대");
        p12.setType("프린트");
        p12.setTime("09:00 - 19:00");
        p12.setLat(35.85913509095088);
        p12.setLng(128.48754291875463);

        Place p13 = new Place();
        p13.setName("구바 지하 1층");
        p13.setBuilding("구바우어관");
        p13.setType("프린트");
        p13.setTime("09:00 - 19:00");
        p13.setLat(35.85639335738497);
        p13.setLng(128.48772138401628);

        Place p14 = new Place();
        p14.setName("의양관 지하 1층");
        p14.setBuilding("의양관");
        p14.setType("프린트");
        p14.setTime("09:00 - 19:00");
        p14.setLat(35.856267774531446);
        p14.setLng(128.4849433084449);

        Place p15 = new Place();
        p15.setName("음대 1F");
        p15.setBuilding("음대");
        p15.setType("프린트");
        p15.setTime("09:00 - 19:00");
        p15.setLat(35.85829876580757);
        p15.setLng(128.4904912789133);

        // 학식당
        Place p16 = new Place();
        p16.setName("구바우어관 지하 1층");
        p16.setBuilding("구바우어관");
        p16.setType("학식당");
        p16.setTime("09:00 - 19:00");
        p16.setLat(35.85423022055133);
        p16.setLng(128.4861118722504);

        Place p17 = new Place();
        p17.setName("우어관 2F");
        p17.setBuilding("우어관");
        p17.setType("학식당");
        p17.setTime("09:00 - 19:00");
        p17.setLat(35.85393360912969);
        p17.setLng(128.48550305451363);

        Place p18 = new Place();
        p18.setName("공대학식당");
        p18.setBuilding("공학관,공대");
        p18.setType("학식당");
        p18.setTime("09:00 - 19:00");
        p18.setLat(35.858219287991645);
        p18.setLng(128.4894519809646);

        Place p19 = new Place();
        p19.setName("아람관 3F");
        p19.setBuilding("아람관");
        p19.setType("학식당");
        p19.setTime("09:00 - 19:00");
        p19.setLat(35.853956594358515);
        p19.setLng(128.48291324663953);

        // 편의점
        Place p20 = new Place();
        p20.setName("CU 계명대명교생활관점");
        p20.setBuilding("명교생활관,기숙사");
        p20.setType("편의점");
        p20.setTime("00:00 - 24:00");
        p20.setLat(35.85705832685428);
        p20.setLng(128.4802146357763);

        Place p21 = new Place();
        p21.setName("이마트24 계명대공학관점");
        p21.setBuilding("공학관,공대");
        p21.setType("편의점");
        p21.setTime("08:30 - 20:00");
        p21.setLat(35.858219287991645);
        p21.setLng(128.4894519809646);

        Place p22 = new Place();
        p22.setName("CU 계명대의과대학점");
        p22.setBuilding("의대");
        p22.setType("편의점");
        p22.setTime("09:00 - 18:00");
        p22.setLat(35.85509916572842);
        p22.setLng(128.4805102964029);

        Place p23 = new Place();
        p23.setName("이마트24 R계명대바우어점");
        p23.setBuilding("구바우어관");
        p23.setType("편의점");
        p23.setTime("00:00 - 24:00");
        p23.setLat(35.85423022055133);
        p23.setLng(128.4861118722504);

        // 기타
        Place p24 = new Place();
        p24.setName("계명항공여행사");
        p24.setBuilding("구바우어관");
        p24.setType("기타");
        p24.setTime("10:00 - 17:00");
        p24.setLat(35.85423022055133);
        p24.setLng(128.4861118722504);

        Place p25 = new Place();
        p25.setName("신바우어관 북카페");
        p25.setBuilding("신바우어관, 북카페");
        p25.setType("기타");
        p25.setTime("10:00 - 18:00");
        p25.setLat(35.85393360912969);
        p25.setLng(128.48550305451363);

        Place p26 = new Place();
        p26.setName("문구점 (구바 B1F)");
        p26.setBuilding("구바우어관, 문구점");
        p26.setType("기타");
        p26.setTime("08:30 - 18:30");
        p26.setLat(35.85423022055133);
        p26.setLng(128.4861118722504);

        Place p27 = new Place();
        p27.setName("우편 취급국 (구바 1F)");
        p27.setBuilding("구바우어관, 우체국, 우편집중국");
        p27.setType("기타");
        p27.setTime("09:00 - 18:00");
        p27.setLat(35.85423022055133);
        p27.setLng(128.4861118722504);

        Place p28 = new Place();
        p28.setName("안경점 (구바 B1F)");
        p28.setBuilding("구바우어관, 안경점");
        p28.setType("기타");
        p28.setTime("10:00 - 16:00");
        p28.setLat(35.85423022055133);
        p28.setLng(128.4861118722504);

        // 검색용 건물
        Place p101 = new Place();
        p101.setName("영암관");
        p101.setBuilding("영암관,인문국제학대학,인국대,사범대");
        p101.setType("건물");
        p101.setTime("");
        p101.setLat(35.85415461789027);
        p101.setLng(128.48402664390682);

        Place p102 = new Place();
        p102.setName("의양관");
        p102.setBuilding("의양관,경영대학");
        p102.setType("건물");
        p102.setTime("");
        p102.setLat(35.85625579439296);
        p102.setLng(128.4850012009452);

        Place p103 = new Place();
        p103.setName("봉경관");
        p103.setBuilding("봉경관,사회과학대학,사과대");
        p103.setType("건물");
        p103.setTime("");
        p103.setLat(35.85523851809555);
        p103.setLng(128.48564639989385);

        Place p104 = new Place();
        p104.setName("쉐턱관");
        p104.setBuilding("쉐턱관,사회과학대학(법학,경찰행정),사과대,인국대");
        p104.setType("건물");
        p104.setTime("");
        p104.setLat(35.859301098309516);
        p104.setLng(128.49014473257003);

        Place p105 = new Place();
        p105.setName("동영관");
        p105.setBuilding("동영관,Keimyung Adams College");
        p105.setType("건물");
        p105.setTime("");
        p105.setLat(35.85325485066771);
        p105.setLng(128.48446094669262);

        Place p106 = new Place();
        p106.setName("백은관");
        p106.setBuilding("백은관,자연과학대,자과대");
        p106.setType("건물");
        p106.setTime("");
        p106.setLat(35.85373347568502);
        p106.setLng(128.48237222708735);

        Place p107 = new Place();
        p107.setName("보산관");
        p107.setBuilding("보산관,약학대학");
        p107.setType("건물");
        p107.setTime("");
        p107.setLat(35.854771210581035);
        p107.setLng(128.48225871445842);

        Place p108 = new Place();
        p108.setName("공학 1호관");
        p108.setBuilding("공대 1호관, 공과대학, 공학 1호관");
        p108.setType("건물");
        p108.setTime("");
        p108.setLat(35.859143003990205);
        p108.setLng(128.4876316274582);

        Place p109 = new Place();
        p109.setName("공학 2호관");
        p109.setBuilding("공대 2호관, 공과대학, 공학 2호관");
        p109.setType("건물");
        p109.setTime("");
        p109.setLat(35.85924712716237);
        p109.setLng(128.48686420006183);

        Place p110 = new Place();
        p110.setName("공학 3호관");
        p110.setBuilding("공대 3호관, 공과대학, 공학 3호관");
        p110.setType("건물");
        p110.setTime("");
        p110.setLat(35.85981413554627);
        p110.setLng(128.4871044977509);

        Place p111 = new Place();
        p111.setName("공학 4호관");
        p111.setBuilding("공대 4호관, 공과대학, 공학 4호관");
        p111.setType("건물");
        p111.setTime("");
        p111.setLat(35.859716674456436);
        p111.setLng(128.4876976993838);

        Place p114 = new Place();
        p114.setName("공학 7호관");
        p114.setBuilding("공대 7호관, 공과대학, 덕래관, 공학 7호관");
        p114.setType("건물");
        p114.setTime("");
        p114.setLat(35.859458496725146);
        p114.setLng(128.48635061868276);

        Place p115 = new Place();
        p115.setName("의과대");
        p115.setBuilding("의과대,의과대학");
        p115.setType("건물");
        p115.setTime("");
        p115.setLat(35.85510670712482);
        p115.setLng(128.48044678670857);

        Place p116 = new Place();
        p116.setName("전갑규관");
        p116.setBuilding("전갑규관,간호대학,인문국제학대학,인국대");
        p116.setType("건물");
        p116.setTime("");
        p116.setLat(35.855309336711976);
        p116.setLng(128.4793602007468);

        Place p117 = new Place();
        p117.setName("스미스관");
        p117.setBuilding("스미스관,Tabula Rasa College");
        p117.setType("건물");
        p117.setTime("");
        p117.setLat(35.85642830201197);
        p117.setLng(128.48362070233372);

        Place p118 = new Place();
        p118.setName("체육관");
        p118.setBuilding("체육관,인문국제학대학,인국대");
        p118.setType("건물");
        p118.setTime("");
        p118.setLat(35.85443051705646);
        p118.setLng(128.48994840717393);

        Place p119 = new Place();
        p119.setName("음대");
        p119.setBuilding("음악공연예술대학,음대");
        p119.setType("건물");
        p119.setTime("");
        p119.setLat(35.852872096471415);
        p119.setLng(128.49062767449203);

        Place p120 = new Place();
        p120.setName("구바우어관");
        p120.setBuilding("구바우어관");
        p120.setType("건물");
        p120.setTime("");
        p120.setLat(35.85425659878625);
        p120.setLng(128.48616494439423);

        Place p121 = new Place();
        p121.setName("우어관");
        p121.setBuilding("우어관");
        p121.setType("건물");
        p121.setTime("");
        p121.setLat(35.853896173955235);
        p121.setLng(128.48543317244648);

        Place p122 = new Place();
        p122.setName("여농관");
        p122.setBuilding("여농관,공대 학생회관");
        p122.setType("건물");
        p122.setTime("");
        p122.setLat(35.85820150914234);
        p122.setLng(128.48943227586744);

        Place p123 = new Place();
        p123.setName("행소박물관");
        p123.setBuilding("행소박물관");
        p123.setType("건물");
        p123.setTime("");
        p123.setLat(35.85669535477981);
        p123.setLng(128.48989393341768);

        placeRepository.deleteAll();
        placeRepository.saveAll(List.of(
            p1,p2,p3,p4,p5,
            p6,p7,p8,p9,p10,
            p11,p12,p13,p14,p15,
            p16,p17,p18,p19,
            p20,p21,p22,p23,p24,p25, p26, p27, p28,
            p101,p102,p103,p104,p105,p106,
            p107,p108,p109,p110,p111,p114,
            p115,p116,p117,p118,p119,p120,
            p121,p122,p123
        ));
    }
}