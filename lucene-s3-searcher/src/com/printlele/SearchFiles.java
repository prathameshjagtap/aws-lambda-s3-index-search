package com.printlele;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.upplication.s3fs.S3FileSystemProvider;

/** Simple command-line based search demo. */
public class SearchFiles implements RequestHandler<String, List<String>> {

	IndexReader reader;

	public SearchFiles() {
		String index = "/<BUCKET_NAME>/<INDEX_LOCATION>/";
		
		//########### OPTIONAL #############################
		// You can directly assign a role to your lambda function
		Map<String, String> env = new HashMap<>();
		env.put(com.upplication.s3fs.AmazonS3Factory.ACCESS_KEY, "ACCESS_KEY");
		env.put(com.upplication.s3fs.AmazonS3Factory.SECRET_KEY, "SECRET_KEY");
		String endpoint = "s3://s3.amazonaws.com/";

		Path path = new S3FileSystemProvider().newFileSystem(URI.create(endpoint), env).getPath(index);

		try {
			reader = DirectoryReader.open(FSDirectory.open(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		reader.close();
	}
	
	@Override
	public List<String> handleRequest(String searchTerm, Context context) {
		
		context.getLogger().log("Input: " + searchTerm);
		
		String field = "FIELD_TO_SEARCH";
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser(field, analyzer);

		List<String> list = new ArrayList<>();
		
		try {
			Query query = parser.parse(searchTerm);
			TopDocs results = searcher.search(query, 100);
			
			for (int i = 0; i < results.scoreDocs.length; i++) {
				int docId = results.scoreDocs[i].doc;
			    Document d = searcher.doc(docId);
			    list.add((i + 1) + ". " + d.get("path"));
			}
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public static void main(String[] args) {
		System.out.println(new SearchFiles().handleRequest("testString", null));
	}
	
}
