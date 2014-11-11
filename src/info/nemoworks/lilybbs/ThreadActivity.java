package info.nemoworks.lilybbs;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.tags.TextareaTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class ThreadActivity extends Activity {

	private String url="";
	private TextView mTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thread);
		
		mTextView=(TextView)this.findViewById(R.id.contentTextView);
		mTextView.setMovementMethod(new ScrollingMovementMethod());
		
		Bundle bundle=getIntent().getExtras();
		url=bundle.getString("URL_LABEL");
		System.out.println("content:"+url);
		new DownloadTask().execute(url);
	}
	
	private class DownloadTask extends AsyncTask<String, Void, String[]> {	
		@Override
		protected String[] doInBackground(String... urls) {
			try {
				return parseContent();
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * Uses the logging framework to display the output of the fetch
		 * operation in the log fragment.
		 */
		@Override
		protected void onPostExecute(String[] results) {
			String result=results[0];
			mTextView.setText(result);
		}
	}
	
	private String[] parseContent() throws ParserException {
		
		String[] results=new String[1];
		// 创建 html parser 对象，并指定要访问网页的 URL 和编码格式
		Parser htmlParser = new Parser(url);
		htmlParser.setEncoding("UTF-8");
		//String postTitle = "";
		String postText="";
		// 获取指定的width 640属性的
		NodeList tableList = htmlParser.extractAllNodesThatMatch(new AndFilter(new NodeClassFilter(TableTag.class),
				new HasAttributeFilter("width", "610")));	

		if (tableList != null && tableList.size() > 0) {
			// 获取指定 div 标签的子节点中的 <li> 节点
			NodeList itemList = tableList
					.elementAt(0)
					.getChildren()
					.extractAllNodesThatMatch(
							(new NodeClassFilter(TableRow.class)), true);

			if (itemList != null && itemList.size() > 0) {
			    int location = 1;
			    if(url.contains("bbscon")){
			        location = 0;
			    }
				NodeList linkItem = itemList
						.elementAt(location)
						.getChildren()
						.extractAllNodesThatMatch(
								new NodeClassFilter(TableColumn.class), true);
				if (linkItem != null && linkItem.size() > 0) {
					// 获取 Link 节点的 Text，即为要获取的推荐文章的题目文字
					TableColumn tc = (TableColumn) (linkItem.elementAt(0));
					NodeList taList=tc.getChildren().extractAllNodesThatMatch(
							(new NodeClassFilter(TextareaTag.class)), true);
					TextareaTag ta=(TextareaTag)taList.elementAt(0);
					postText=ta.getStringText();

					//postTitle = tc.getText();
					//postText = tc.getStringText();
					//System.out.println(postText);
					//System.out.println(postHref);
					results[0]=postText;
				}
			}
		}

		return results;
	}

	
}