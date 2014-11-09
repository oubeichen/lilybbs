package info.nemoworks.lilybbs;

import java.util.ArrayList;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ContentFragment extends Fragment {

    private int mType;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mType = getArguments().getInt("FETCH_TYPE");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mListView = (ListView) inflater.inflate(R.layout.fragment_content,
                container, false);
        switch (mType) {
        case 1:
            new DownloadTask()
                    .execute("http://bbs.nju.edu.cn/vd75455/board?board=D_Computer");
            break;
        default:
            new DownloadTask().execute("http://bbs.nju.edu.cn/bbstop10");
        }
        return mListView;
    }

    private class DownloadTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... urls) {
            try {
                return parseContent(urls[0]);
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
            // Log.i(TAG, result);
            if(results != null){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getActivity(), android.R.layout.simple_list_item_1, results);
                mListView.setAdapter(adapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String[] parseContent(String url) throws ParserException {
        ArrayList<String> pTitleList = new ArrayList<String>();
        // 创建 html parser 对象，并指定要访问网页的 URL 和编码格式
        Parser htmlParser = new Parser(url);
        htmlParser.setEncoding("UTF-8");
        String postTitle = "";
        if (mType == 0) {// 十大
            // 获取指定的 div 节点
            NodeList toptable = htmlParser
                    .extractAllNodesThatMatch(new AndFilter(
                            new NodeClassFilter(TableTag.class),
                            new HasAttributeFilter("width", "640")));

            if (toptable != null && toptable.size() > 0) {
                // 获取指定 div 标签的子节点中的 <tr> 节点
                NodeList itemTopList = toptable
                        .elementAt(0)
                        .getChildren()
                        .extractAllNodesThatMatch(
                                (new NodeClassFilter(TableRow.class)), true);

                if (itemTopList != null && itemTopList.size() > 0) {
                    for (int i = 0; i < itemTopList.size() - 1; ++i) {
                        // 在 <tr> 节点的子节点中获取 Link 节点
                        NodeList linkItem = itemTopList
                                .elementAt(i)
                                .getChildren()
                                .extractAllNodesThatMatch(
                                        new NodeClassFilter(TableColumn.class),
                                        true);
                        if (linkItem != null && linkItem.size() > 0) {
                            // 获取 Link 节点的 Text，即为要获取的推荐文章的题目文字
                            postTitle = ((LinkTag) ((linkItem.elementAt(7))
                                    .getChildren().elementAt(0))).getLinkText();
                            pTitleList.add(postTitle);
                        }
                    }
                }
            }
        } else if (mType == 1) {// 计算机系版
            // 获取指定的 div 节点
            NodeList toptable = htmlParser
                    .extractAllNodesThatMatch(new AndFilter(
                            new NodeClassFilter(TableTag.class),
                            new HasAttributeFilter("width", "670")));

            if (toptable != null && toptable.size() > 2) {
                // 获取指定 div 标签的子节点中的 <tr> 节点
                NodeList itemTopList = toptable
                        .elementAt(2)
                        .getChildren()
                        .extractAllNodesThatMatch(
                                (new NodeClassFilter(TableRow.class)), true);

                if (itemTopList != null && itemTopList.size() > 0) {
                    for (int i = 1; i < itemTopList.size() - 1; ++i) {
                        // 在 <tr> 节点的子节点中获取 Link 节点
                        NodeList linkItem = itemTopList
                                .elementAt(i)
                                .getChildren()
                                .extractAllNodesThatMatch(
                                        new NodeClassFilter(TableColumn.class),
                                        true);
                        if (linkItem != null && linkItem.size() > 0) {
                            // 获取 Link 节点的 Text，即为要获取的推荐文章的题目文字
                            postTitle = ((LinkTag) ((linkItem.elementAt(5))
                                    .getChildren().elementAt(0))).getLinkText();
                            pTitleList.add(postTitle);
                        }
                    }
                }
            }
        }
        String[] results = new String[pTitleList.size()];
        pTitleList.toArray(results);
        return results;
    }
}
