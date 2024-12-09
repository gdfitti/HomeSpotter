package org.uvigo.esei.example.homespotter.imgbb;

public class ImgBBResponse {
    public Data data;
    public String status;

    public class Data {
        public String id;
        public String url;
        public String display_url;
        public String delete_url;
    }
}
