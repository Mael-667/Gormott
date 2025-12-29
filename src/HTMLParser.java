import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HTMLParser {

    //les tableaux statiques dans une fonction qui se fait appeler en boucle se font recréer en boucle donc a éviter
    private static final String[] exceptions = {"img", "br", "!DOCTYPE", "meta", "link"};

    
    public static HTMLNode Parser(String url){
        HTMLNode root = new HTMLNode(null);
        // On définit manuellement le premier noeud qui englobera tout puis on laisse le parser s'occuper du reste
        try (BufferedReader in = new BufferedReader(new FileReader(url), 16384)) {
            root.name = "root";
            getContent(in, root);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    // Récupère récursivement le contenu des balises html
    private static void getContent(BufferedReader in, HTMLNode node) throws IOException{
        int input;
        char lastC = ' ';
        int openChevronIndex = 0;
        StringBuilder sb = new StringBuilder(256);
        // Récupère les infos de la balises et son contenu puis définit tout une fois que la balise a été fermée
        while((input = in.read()) > 0){
            char c = (char) input;
            if(lastC == '<' && c == '/'){
                sb.deleteCharAt(sb.length()-1);
                closeBalise(in, node);
                break;
            } else if(c == '<' && lastC != '\\'){
                sb.append(c);
                openChevronIndex = sb.length();
            } else if(c == '>' && openChevronIndex != 0 && lastC != '\\'){
                String balise = sb.substring(openChevronIndex, sb.length());
                sb.delete(openChevronIndex-1, sb.length());
                openChevronIndex = 0;

                HTMLNode childNode = new HTMLNode(node);
                baliseParser(childNode, balise);

                if(!soloBalise(balise)) getContent(in, childNode);

            } else {
                sb.append(c);
            }
            lastC = c;
        }
        node.setTextContent(sb.toString());
    }


    private static boolean baliseParser(HTMLNode node, String balise){
        boolean isException = false;
        //to resolve : tag type, class, id, default parameter, deferredRender
        //appliquer l'héritage css ici via node.parent.style
        //gerer les gradient avec des images gen en java
        node.name = balise;

        return isException;
    }

    private static void closeBalise(BufferedReader in, HTMLNode node) throws IOException{
        int input;
        while((input = in.read()) > 0 && input != (int) '>'){
        }
    }

    private static boolean soloBalise(String balise){
        for (int i = 0; i < exceptions.length; i++) {
            if(balise.startsWith(exceptions[i])) return true;
        }
        return false;
    }









    // private static ReturningValues isInString(boolean inString, char c, char limiter){
    //     if(c != '\'' && c != '"') return new ReturningValues(inString, limiter);
    //     if(inString == false){
    //         if(c == '\''){
    //             inString = true;
    //             limiter = '\'';
    //         } else if(c == '"'){
    //             inString = true;
    //             limiter = '"';
    //         }
    //         return new ReturningValues(inString, limiter);
    //     } else {
    //         if(c == '\'' && limiter == '\''){
    //             inString = false;
    //             limiter = 1;
    //         } else if(c == '"' && limiter == '"'){
    //             inString = false;
    //             limiter = 1;
    //         }
    //         return new ReturningValues(inString, limiter);
    //     }
    // }

    private static boolean trim(char lastC, char c){
        char[] exceptions = {' ', '\r', 10, 13, 9};
        boolean isException = false;
        for (int i = 0; i < exceptions.length; i++) {
            if(lastC == exceptions[i]){
                for (int j = 0; j < exceptions.length; j++) {
                    if(c == exceptions[j]){
                        isException = true;
                    }
                }
            }
        }
        return isException;
    }
}
