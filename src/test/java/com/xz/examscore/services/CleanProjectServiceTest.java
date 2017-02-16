package com.xz.examscore.services;

import com.hyd.simplecache.utils.MD5;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author by fengye on 2016/9/3.
 */
public class CleanProjectServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    CleanProjectService cleanProjectService;

    @Test
    public void testDoCleanData() throws Exception {
        String[] projectIds = new String[]{
                "430600-01307dd66a314b5f86dabcfd088ba0ac",
                "430600-ad8914c1b1f34e1cb98bf1239572d171",
                "430800-b43d5f6e56ef4917af85842cf4095ae3",
                "430100-59c4d32dcf0049a3bd18efdc0cc982c6",
                "430100-fb193657d4d94066a6677f52fc0bc9ce",
                "430100-0853da0051234978a599b2198ef56236",
                "430100-1e9564ccd9e0436e9ba9792b1c5db459",
                "430100-10557f2a53cb420fba115eb53ffb7693",
                "430100-2df3f3ad199042c39c5f4b69f5dc7840",
                "430100-4a0ff3a351e14590973c3551a7e73781",
                "430100-047a9cc248244e4e95b2fbc64074252f",
                "430100-2563f38f6595405ab0f59912edcd44ad",
                "430100-405f6648c79545d589687a1d0763fc9f",
                "430100-7961eceab669412888d2df1792f024ec",
                "430100-b2a0dce057d241bda0085ea9be71bfcf",
                "430100-3267987f5eb34ebc808581dca4c3a26d",
                "430100-7ea97ce17cc048f7bc53300cab6bb422",
                "430100-5eaae26271434bdeb2850b0cc3e4e1f3",
                "430100-0204f1b9cee4471cb961f131798b3da0",
                "430100-ee1399b46b1c42e79de6a7bb6ecd3a12",
                "430100-fdba0a8b6dba491ea697ee56a846ea73",
                "430100-f2b726c943ed478cafe83e94666b69f5",
                "430100-710cd258c9474f5ba3e2ae6f4e5eb5bc",
                "430100-86a15b42b60c431990a97943d29e471c",
                "430100-6db9deaeafd6491695c0ec1680af29b6",
                "430100-d812e91037c74565bf01dbfc61908c04",
                "430100-311a82c03cf347a1961f8ed5a0df5af1",
                "430100-af31724226984036b34e93d51c22840f",
                "430100-6f10a43d5e41437b9a244293a184deeb",
                "430100-2237c3348d96449fa52d782d169fd804",
                "430100-cd7d236d77ed4f62ab7ba4b45bbf9d34",
                "430100-348109e7fd7746fcaa681852afd3c273",
                "430100-115cf65fdba84c498dd9d65db7dc7282",
                "430100-c021a9781db64803810695dbaa55e5d5",
                "430100-6b082439d54a409eae7bc2c4055b5a2e",
                "430100-fe0af4e90cec429bbe147df40364fcb2",
                "430100-6cdfcbd4aa724ca6a84aa7504f69844f",
                "430100-246c187b75bb4918bc2b5bbaf4a18d12",
                "430100-8550c380c8594d18b5cb14a08e4645de",
                "430100-c8a5bf6612e54c0ba0fe7466d7940296",
                "430100-c88d7de7a2c7482caf8f779777d12f92",
                "430100-25e02c9f34ea4966817231473f384ecc",
                "430100-87be7091893f47b4b17c58aa73b568e3",
                "430100-5838014fdb094b26aa9bbc5bc0a092a5",
                "430100-9623a85393334994808bed9d701ff650",
                "430100-fd71464285394d439e38bf95d3aee667",
                "430100-ff8f15d0a3d64cbc8696e7d3b11e588e",
                "430100-5f0f1b59e51c4863aad0c354ead7be40",
                "430100-9c303017eeae46c5b200ba9e754cf759",
                "430100-075c6c666c2b4e1496e6616566841c54",
                "430100-13b0bde6727d464dabf278dc26d1b3c1",
                "430100-e6a59fea07da42b39c7317e88d442b15",
                "430100-5e259d35fa364a0c823d248850ae4e38",
                "430100-19f76981e90a445889e95e0027f29b08",
                "430100-f608e462490b48ef89170a435d4e69d0",
                "430100-553137a1e78741149104526aaa84393e",
                "430100-587961c5375d453691475d267d12fb02",
                "430100-07f6839459964f308dcafaf784b6341c",
                "430100-801650c7bdd648bc94b2a4ead443aab5",
                "430100-4bf71a7e8c3c4decb760aab9033d021d",
                "430100-52f20ee4565b48f98399deca460b72a0",
                "430100-bb14a9a3dbad40fa80451874b8f6a565",
                "430100-c432e3ab308b4d22b84cd619ac3efa8f",
                "430100-764926a000864b59996e4cf8007b2115",
                "430100-50d3a1f74f5843c8a8c4965d2541616b",
                "430100-0e7608e18d1546ce9375434f926a1f87",
                "430100-218065d89a044f36a53cbd5a460a5feb",
                "430100-36b87d2b90084d39b57727873ac1ab39",
                "430100-1809358f5d3d417ab52df2c2f07257f0",
                "430100-462c6c3e89f7426cb1158d0545153df6",
                "430100-963ca087f2604c7e9b26b9852867e868",
                "430100-986d7df579d6407f8b350149c677b49a",
                "430100-deecc1c49c9741e183395620f326de77",
                "430100-98fccb03bb0f458d978c2deef9c89039",
                "430100-569efda89ede421dba988744086e7782",
                "430100-0f15247fff154f0d8fefa28aec6bd6f7",
                "430100-93f5e1070db84085ae5309be73adbef5",
                "430100-67aba32145124c70891c8f12b2abbe1f",
                "430100-df917037dc2b464d96d1b0e190b26967",
                "430100-0a2b41ccafff46c2a2c6b4b501e51642",
                "430100-71f1704acef34e15801e0913f50bce3a",
                "430100-6ebf6d5d903c499aa8b7209af0d56b3f",
                "430100-a78a195640534cf79265364c11864ff2",
                "430100-cb04005aa5ae460fae6b9d87df797066",
                "430500-085476020b244d3da2be1b7b32c2abff",
                "430100-62c23c735c4742a0964496c4e2f54ca9",
                "433100-580e8c226f694f849a05a91b551b77b9",
                "430100-a05db0d05ad14010a5c782cd31c0283f",
                "433100-0372aa59ae4841618138c65e9ee18314",
                "430100-7fe2bc3f71754052b20c89f2858ed4c8",
                "430100-b50c09a09dbc4aac820cecc98c402522",
                "430100-876611585de1489e8b6c592f4a742337",
                "430100-95d9a25ea67c417f85a3a8c425fa5ba2",
                "430100-f8ca88deacf24292977e0bd392d2ffed",
                "430100-e7aa00bf243543d4a1901bbfa0bb4d6b",
                "430100-7d977fb362ca4fb0b544f2a0b142107d",
                "430100-20b8352de5a44ea7a62baa4fda4223ea",
                "430100-7c6278b497d5477e92f4f673d500fd76",
                "430100-c4a8dcbb95624588b17918e48a051038",
                "430100-e79bed7ccf5d4eb9acfa38fc09eea917",
                "430100-9f41b67c5d8d42229ae7132abc0979ec",
                "430100-379be8c302a64c748f3e62cb22e5d9b5",
                "430100-f59759fddfd1400888ce77be54dfd116",
                "433100-6d6cd2cc2aa04de998e3216cd17ee5d1",
                "430100-bc361493a24d456bb19a990cb00dda8b",
                "430100-d4389856ff434b0087908a89dce4bc5d",
                "430100-c9ccbcb7fcb542e3a2f278e8d2ca2c4f",
                "430100-a7cf36bad6b5488daa5ee517a04c257d",
                "430100-ab702842133a45a6bc2fa5cecbed5327",
                "430100-1637ccc47d8f4180859dc2940fb7fc14",
                "430100-7ed67368c4444041b46d7aeff8d180d1",
                "430100-a08472850a62414f94caeb467faddc12",
                "430100-feb8ca98e47c4c2188e5f7f220beca12",
                "430100-097153b8db774db99fbffc587540d271",
                "430100-1bc079dae1604d499937386bfc5a0714"
        };
        for(String projectId : projectIds){
            cleanProjectService.doCleanSchedule(projectId);
        }
    }

    @Test
    public void testGetAllCollections() throws Exception {
        List<String> list = cleanProjectService.getAllCollections();
        Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
        System.out.println(list.toString());
    }

}