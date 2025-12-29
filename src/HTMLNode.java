import java.util.ArrayList;

public class HTMLNode {
    
    //definition
    public String name;
    public String textContent;
    public String id;
    public final ArrayList<String> HTMLClass = new ArrayList<>();

    //style
    public final CSS style = new CSS();
    

    //relation
    public HTMLNode parent;
    public final ArrayList<HTMLNode> children = new ArrayList<>();
    
    public HTMLNode(HTMLNode parent){
        this.parent = parent;
        if(parent != null) parent.addChild(this);
    }

    public void addChild(HTMLNode child){
        children.add(child);
    }

    public void setTextContent(String s){
        this.textContent = style.trim ? s.trim() : s;
    }

    // public String toString(){
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("name : "+name+" textcontent : "+textContent+" nb of child : "+children.size()+" child of "+parent+": \r");
    //     return sb.toString();
    // }
}
