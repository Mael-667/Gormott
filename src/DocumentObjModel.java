public class DocumentObjModel {
    private HTMLNode document;
    public DocumentObjModel(String url){
        this.document = HTMLParser.Parser(url);
    }
}
