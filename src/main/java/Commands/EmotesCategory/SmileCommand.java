package Commands.EmotesCategory;
import CommandListeners.CommandProperties;
import Commands.EmoteAbstract;

@CommandProperties(
        trigger = "smile",
        emoji = "\uD83D\uDE04",
        executable = true,
        aliases = {"happy"}
)
public class SmileCommand extends EmoteAbstract {

    protected String[] getGifs() {
        return new String[]{"https://media1.tenor.com/images/5b69fcd25e2467003e675cf32a232260/tenor.gif?itemid=8808104",
                "https://media1.tenor.com/images/ba7c28c45c0123e95fbdf0854cbc7861/tenor.gif?itemid=12869746",
                "https://media1.tenor.com/images/aeb0ef81524736ebb6c1881398e076b7/tenor.gif?itemid=13946208",
                "https://media1.tenor.com/images/89289af19b7dab4e21f28f03ec0faaff/tenor.gif?itemid=12801687",
                "https://media1.tenor.com/images/ce2e90deb63595d21d4ddf0a8f4ba6e7/tenor.gif?itemid=15795593",
                "https://media1.tenor.com/images/a784d25bc90f5e81ac6615f2b165d2e6/tenor.gif?itemid=9097669",
                "https://media1.tenor.com/images/c5fad21f9828d19044a58f8b84a6e14b/tenor.gif?itemid=6013419",
                "https://media1.tenor.com/images/64e0528a06b474ffb14525c437da2544/tenor.gif?itemid=11031890",
                "https://media1.tenor.com/images/06a472766b5c90c0959a572eaaa6fb4b/tenor.gif?itemid=8073510",
                "https://media1.tenor.com/images/55dde6c4f1eaca6b1e52626b980c0074/tenor.gif?itemid=13576447",
                "https://media1.tenor.com/images/c8d13a4636c548e962d8d4fdb0eaa169/tenor.gif?itemid=12217236",
                "https://media1.tenor.com/images/4e0a400d7621b5452854bcae00d9a98e/tenor.gif?itemid=5723668",
                "https://media1.tenor.com/images/cc66ae959cb51c118c782325fcdc4f3f/tenor.gif?itemid=9869247",
                "https://media1.tenor.com/images/ecc8d5665f2698529d63b7c7c55fb5fc/tenor.gif?itemid=8674277",
                "https://media1.tenor.com/images/123df3be1acfe3306b91e9c3dd6f9438/tenor.gif?itemid=5322596",
                "https://media1.tenor.com/images/8a549e6d7066bbc0aeb63d7c69a42c27/tenor.gif?itemid=4838963",
                "https://media1.tenor.com/images/6b353c18a4628d3d2346d031591296fa/tenor.gif?itemid=12803100",
                "https://media1.tenor.com/images/d627d2facd06abb496f97c5943b2f9ae/tenor.gif?itemid=11346577",
                "https://media1.tenor.com/images/9411ce1ef75d43771bf0f305e7eb6487/tenor.gif?itemid=12793368",
                "https://media1.tenor.com/images/8bd1588e665e2c33df39736b2d2c0e12/tenor.gif?itemid=12823757",
                "https://media1.tenor.com/images/bb0cbe662c9c7fb3bd59e75a7214475d/tenor.gif?itemid=4838964",
                "https://media1.tenor.com/images/e9808bd93cc8961ef81e6fa8ae560046/tenor.gif?itemid=13857197",
                "https://media1.tenor.com/images/29b71255760361c5f6c40f089847b1ab/tenor.gif?itemid=7338963",
                "https://media1.tenor.com/images/683b40a9da270868366b0b43541ec2c7/tenor.gif?itemid=5109317",
                "https://media1.tenor.com/images/a9c114df59d644d43e1da6f3e7db66ca/tenor.gif?itemid=4838961",
                "https://media1.tenor.com/images/325b3ba6a2beabe21c79b54c6de4e2c7/tenor.gif?itemid=15060821",
                "https://media1.tenor.com/images/94cd0ea149daf82c6e6af8c444c40eb4/tenor.gif?itemid=8933103",
                "https://media1.tenor.com/images/2dff8879b3aa16404eedf586827c4085/tenor.gif?itemid=5109617",
                "https://media1.tenor.com/images/032c3c7ceefe2a6f43a65e35e37e99f3/tenor.gif?itemid=4874809",
                "https://media1.tenor.com/images/3cb1bfa449cb8feb07a900353db5357e/tenor.gif?itemid=13576448",
                "https://media1.tenor.com/images/05e914f592699a142085a111eb985685/tenor.gif?itemid=5900593",
                "https://media1.tenor.com/images/3a5314f44cbb30a3c7c55a8c136f1f2d/tenor.gif?itemid=5795854",
                "https://media1.tenor.com/images/8c7eb725d0dec053d48a6dbd75c2303a/tenor.gif?itemid=14247095",
                "https://media1.tenor.com/images/fa2f0d664c45cbba388619fa650fe013/tenor.gif?itemid=14207769",
                "https://media1.tenor.com/images/a7e87466022015e036c06c3927c251f9/tenor.gif?itemid=8971744",
                "https://media1.tenor.com/images/5bbfe72d3faca25bdac64d85442ff553/tenor.gif?itemid=15516760",
                "https://media1.tenor.com/images/dd11b8313236dba83c954c3f0cb0083a/tenor.gif?itemid=6202171",
                "https://media1.tenor.com/images/37dc1c93e11e2bc4dae97acf90a7a512/tenor.gif?itemid=6196824",
                "https://media1.tenor.com/images/4a9282975b440f0f27e8bc6284d7f57e/tenor.gif?itemid=12799731",
                "https://media1.tenor.com/images/6b9f31a199a38cfd900209425265343e/tenor.gif?itemid=13451329",
                "https://media1.tenor.com/images/2ae3513a23eb3da009c875633d7b1dc7/tenor.gif?itemid=13451301",
                "https://media1.tenor.com/images/d259f91eacb17bb6b8f406e8be0d4812/tenor.gif?itemid=14331014",
                "https://media1.tenor.com/images/c161c8fe12d8809f216c7dc29c14de40/tenor.gif?itemid=14252803",
                "https://media1.tenor.com/images/62eeebd592412da8c543478da1334bd3/tenor.gif?itemid=12395657",
                "https://media1.tenor.com/images/4c739675a1d6585428ac26549f624374/tenor.gif?itemid=12137071",
                "https://media1.tenor.com/images/e7adcb26963357ccd93bda31f15ea16f/tenor.gif?itemid=3474483",
                "https://media1.tenor.com/images/80fa18ad793e462a0edb9ca01f535060/tenor.gif?itemid=15421729",
                "https://media1.tenor.com/images/52ea7d449a5402030a3432fd3c94aa99/tenor.gif?itemid=13119051",
                "https://media1.tenor.com/images/b63941fc92c0c4f741596b709883c0bf/tenor.gif?itemid=15150337",
                "https://media1.tenor.com/images/cd2b226b8a037f39cb8096d8c745ab99/tenor.gif?itemid=13119047",
                "https://media1.tenor.com/images/4fd0125d511e202a50bd41e120187506/tenor.gif?itemid=7374641",
                "https://media1.tenor.com/images/2c9307ba394444d476828d68db43bf4a/tenor.gif?itemid=12003938",
                "https://media1.tenor.com/images/5def36b97e1d73b00429da29cedbee72/tenor.gif?itemid=12799468",
                "https://media1.tenor.com/images/f782506ae4ec77052b4a74b577c1e8fd/tenor.gif?itemid=15757074",
                "https://media1.tenor.com/images/0e28ae970b4a55c23b4a4b4c5f17cbe1/tenor.gif?itemid=15735532",
                "https://media1.tenor.com/images/c4748521d15c2aeb67dc3b11f50ac5cb/tenor.gif?itemid=15792742",
                "https://media1.tenor.com/images/6ac51389b29aa4392c628080ff6b5545/tenor.gif?itemid=14820877",
                "https://media1.tenor.com/images/ef35757d49ee947ae255e87ca9e1058f/tenor.gif?itemid=14707996"
        };
    }

}