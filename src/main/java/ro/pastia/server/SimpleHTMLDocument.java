package ro.pastia.server;

import java.io.IOException;

/**
 * Created by Pastia on 17.07.2014.
 */
public class SimpleHTMLDocument implements Appendable {
  private String title;

  private StringBuilder body = new StringBuilder();

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body.toString();
  }

  public void setBody(String body) {
    this.body = new StringBuilder(body);
  }

  @Override
  public Appendable append(CharSequence csq) {
    body.append(csq);
    return this;
  }

  @Override
  public Appendable append(CharSequence csq, int start, int end) {
    body.append(csq,start,end);
    return this;
  }

  @Override
  public Appendable append(char c) {
    body.append(c);
    return this;
  }

  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("<html>\n");
    if((title != null) && (title.length() != 0)){
      sb.append("<head>\n<title>" + title + "</title>\n</head>\n");
    }
    sb.append("<body>\n");
    sb.append(body);
    sb.append("</body>\n</html>");

    return sb.toString();
  }


}
