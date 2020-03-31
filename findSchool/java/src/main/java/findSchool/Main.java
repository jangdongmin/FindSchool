package findSchool;
  
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import kr.bydelta.koala.data.Morpheme;
import kr.bydelta.koala.data.Sentence;
import kr.bydelta.koala.data.Word;
import kr.bydelta.koala.eunjeon.Tagger;

public class Main {
	
	static HashMap<String, Integer> totalResult = new HashMap<String, Integer>();
	
	static HashSet<String> naturalSearch = new HashSet<String>();
	static HashSet<String> learningData = new HashSet<String>();
	
	//학교 구분자
	static String[] firstKeyword = {"초등학교", "중학교", "고등학교", "대학교"};
	static String[] secondKeyword = {"초", "중", "고", "대"};
 
	//홍익대학교사범대학부속여자중
	static String[] attachSchoolKeyword = {"대학교","여대", "대학부설"};
	static String[] sexKeyword = {"여자","남자"};
	
	//출력할때 같은 학교 구분 할떄 쓰임.
	//단어 줄임말 확장을 위해서 쓰임.
	static HashMap<String, String> extentionKeyword = new HashMap<String, String>()
	{
	   {
		   //경산여고, 경산여자고등학교
		   put("여대", "여자대학교");
		   put("여고", "여자고등학교");
		   put("여중", "여자중학교");
		   put("체고", "체육고등학교");
		   
		   //예외사항.
		   //동여중, 동두천여자중학교
		   put("동여중", "동두천여자중학교");
	   }
	};
	
	//예외처리( 제거 ) 단어.
	static String[] replaceKeyword = {"중학생"};//이 단어는 최종에서 제거하자.
	
	static String[] exceptionKeyword = {"저희","인천서구","광역시도","재학중","중고등학교","우리","수능","로딩","공부","아닌","아닌고","두신대"};
	static String[] stopKeyword = {"전국대","이번에중", "유리중","기억중","학교중","학생중", "마지막초", "시험대", "줄넘기대", "사용중", "합창대" ,"세면대", "서로에대", "기본중", "다른중","학교사범대","서울중","은홍강은비강전수진전전고","당하고"};
	
	//예외처리( 추가 ) 단어.
	static String[] addKeyword = {"백현중","대전신일여중","사직여중","인천체고"};
	 
	//특수 학교들 구분하기 위해 쓰임.
	static String[] specialSchoolKeyword = {"국제통상", "사관", "미용", "관광", "디자인", "부속", "인터넷", "과학", "미디어", "EB", "정보", "예술","예고", "제철", "체육", "사대부", "비즈니스", "상업", "부설"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//	 	 
//		안녕하세요 장동민입니다. 
//		맥의 이클립스에서 작업을 했습니다. (gradle) 
// 		실행하시면 결과는 findSchool/java/result.txt 로 출력됩니다.  (log로도 출력됩니다.)
//		실행이 안되시거나 문제가 있으시면 연락주세요.
//		잘 부탁드립니다.
//		감사합니다.
//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
//		1. csv 파일 읽기 
//			- 2개의 String을 얻음.
//			1) 특수문자 제거한 String
//			2) 모든 문자 데이터 띄어쓰기 붙힌 String ex. 수원창현고짜장면먹고싶어요사진은제가먹다남은짬뽕이네요재학중은아니지만모교학생분들의배를불
//		2. 1번째 검색(keywordSearch). 
//			- 키워드는 초등학교, 중학교, 고등학교, 대학교
//			- 해당 키워드로 검색된 단어는 learningData 데이터 존재여부를 확인 후 
//			- 자연어 처리 모듈을 활용하여 불필요한 단어 제거
//			- 처리 완료된 데이터는 learningData에 넣어둠.  (자연어 처리가 느려서 최대한 사용 안하게끔)
//				ex. 삼육중학교가 검색되었다면,  삼육중학교, 삼육중, 삼육고, 삼육초, 삼육대  이런식으로 추가 저장. 
//		3. 2번째 검색.  
//			- 키워드는 초, 중, 고, 대
//			- 검색된 단어는 learningData 데이터 존재여부를 확인 후 
//			- 자연어 처리 모듈을 활용하여 불필요한 단어 제거
//			- 마찬가지로 처리 완료된 데이터는 learningData에 넣어둠. (자연어 처리가 느려서 최대한 사용 안하게끔)
//		4. learningData을 이용하여 (모든 문자 데이터 띄어쓰기 붙힌 String) 에서 학교를 검색. 
//		5. 출력.
//////////////////////////////////////////////////////////////////////////////////////////
		
		
//////////////////////////////////////////////////////////////////////////////////////////
//		첫번째 검색
//////////////////////////////////////////////////////////////////////////////////////////

		BufferedReader bReader = null;
		StringBuilder everything = new StringBuilder();
		
		File file = null;
		try
		{
			file = new File("src/main/java/findSchool/comments.csv");
	        bReader = new BufferedReader(new FileReader(file));
	         
			
	        while (true)
	        {
	        	String text;
	        	try
	        	{
	        		text = bReader.readLine();
	        	}
	        	catch (Exception e)
	        	{
	        		text = null;
	        	}

	        	try
	    		{
	        		text = StringReplace(text);

	        		keywordSearch(text, firstKeyword, false, 1);
		        	
		        	everything.append(text.replaceAll(" ", ""));
	    		}
	        	catch(Exception e) 
	        	{
	        		break;
	        	}
	        }
	        
		} catch(IOException e) {
			System.out.println(e);
	        e.printStackTrace();
	    }
		finally
		{ 
	        try
	        {
	            if(bReader != null) 
            	{
	            	bReader.close();	            	 
            	}
	        }
	        catch(IOException e) 
	        {
	            e.printStackTrace();
	        }
		}
 
//////////////////////////////////////////////////////////////////////////////////////////
//두번째 검색
//////////////////////////////////////////////////////////////////////////////////////////
		
		try
		{
			bReader = new BufferedReader(new FileReader(file));
			while (true)
			{
	        	String text;
	        	try
	        	{
	        		text = bReader.readLine();
	        	}
	        	catch (Exception e)
	        	{
	        		text = null;
	        	}
	        	
	        	try
	    		{
	        		text = StringReplace(text);
	        		
	        		keywordSearch(text, secondKeyword, true, 2);
	    		}
	        	catch(Exception e) 
	        	{
	        		break;
	        	} 
	        }
	        
		} catch(IOException e) {
			System.out.println(e);
	        e.printStackTrace();
	    }
		finally
		{ 
	        try
	        {
	            if(bReader != null) 
            	{
	            	bReader.close();	            	 
            	}
	        }
	        catch(IOException e) 
	        {
	            e.printStackTrace();
	        }
		}
		
//////////////////////////////////////////////////////////////////////////////////////////
//예외처리 
//애매한 단어들.
//////////////////////////////////////////////////////////////////////////////////////////
		
		//검색안된, 예외 학교를 넣자.
		for(int i = 0; i < addKeyword.length; i++)
		{
			insertData(addKeyword[i]);	
		}

//////////////////////////////////////////////////////////////////////////////////////////
//결과 계산
//////////////////////////////////////////////////////////////////////////////////////////
		
		searchWord(everything);

//////////////////////////////////////////////////////////////////////////////////////////
//출력
//////////////////////////////////////////////////////////////////////////////////////////
		resultPrint();
	}
	
	public static void resultPrint()
	{
		createTextFile();
	}
	
	public static void searchWord(StringBuilder everything)
	{
		String allStringData = everything.toString();
		HashMap<String, Integer> sort = new HashMap<String, Integer>();
		
		for(int i = 0; i < replaceKeyword.length; i++)
    	{
			allStringData = everything.toString().replaceAll(replaceKeyword[i], "");
    	}
		
		for(String school: learningData)
		{
        	String searchStr = allStringData;
        	 
        	//학교 뒤에 두글자를 잘라서,
        	//여고, 여중, 등을 -> 여자고등학교, 여자중학교로 교체
        	
        	//단어 줄임말 확장을 위해서 쓰임...
        	//ex. 동여중, 동두천여자중학교
        	String extention = extentionKeyword.get(school);
        	
        	boolean schoolCuttingUse = false;
        	String schoolCutting = "";
        	if(extention == null)
        	{
	        	schoolCutting = school.substring(school.length()-2 ,school.length());
	        	if(extentionKeyword.get(schoolCutting) != null)
	        	{
	        		schoolCuttingUse = true;
	        		String str = school.substring(0, school.length()-2);
					schoolCutting = str + extentionKeyword.get(schoolCutting);
	        	}
        	}
        	else
        	{
        		schoolCuttingUse = true;
        		schoolCutting = extention;
        	}

        	while(true)
        	{
        		int frontIndex = searchStr.indexOf(school);
	        	
	        	if(frontIndex == -1)
    			{
	        		break;
    			}
	        	else
	        	{
	        		//schoolCuttingUse = true이면 
	        		//여고, 여중, 등을 -> 여자고등학교, 여자중학교로 교체
	        		if(schoolCuttingUse)
	        		{
	        			if(sort.get(schoolCutting) == null)
			        	{
		        			sort.put(schoolCutting, 1);
			        	}
			        	else
			        	{
			        		sort.put(schoolCutting, sort.get(schoolCutting)+1);
			        	}
	        		}
	        		else
	        		{
	        			if(sort.get(school) == null)
			        	{
		        			sort.put(school, 1);
			        	}
			        	else
			        	{
			        		sort.put(school, sort.get(school)+1);
			        	}
	        		}
	        		
        			searchStr = searchStr.substring(frontIndex+school.length(), searchStr.length());
	        	}
        	}
        }
		
		//학교 중복 제거를 위해 루프를 돈다.
		//ex) 문정중학교, 문정중
		//ex) 서곶중학교, 서곶중
		Set<Entry<String, Integer>> set = sort.entrySet();
    	Iterator<Entry<String, Integer>> it_src = set.iterator();
    	 
    	while (it_src.hasNext()) 
    	{
    		Map.Entry<String, Integer> src = (Map.Entry<String, Integer>)it_src.next();
    		 
    		Iterator<Entry<String, Integer>> it_dst = set.iterator();
    		
    		String srcKey = src.getKey();
			int srcValue = src.getValue();
			
    		while (it_dst.hasNext()) 
        	{
        		Map.Entry<String, Integer> dst = (Map.Entry<String, Integer>)it_dst.next();
        		
        		if(!srcKey.equals(dst.getKey()) && srcKey.contains(dst.getKey()))
        		{
        			if(classJudge(srcKey) == classJudge(dst.getKey()))
					{
        				if(srcKey.length() < dst.getKey().length())
            			{
            				srcKey = dst.getKey();
            			}
            			
            			if(srcValue < dst.getValue())
            			{
            				srcValue = dst.getValue();
            			}
					}
        		}
        		else if(!srcKey.equals(dst.getKey()) && dst.getKey().contains(srcKey))
        		{
        			if(classJudge(srcKey) == classJudge(dst.getKey()))
					{
        				if(srcKey.length() < dst.getKey().length())
            			{
            				srcKey = dst.getKey();
            			}
            			
            			if(srcValue < dst.getValue())
            			{
            				srcValue = dst.getValue();
            			}
					}
        		}
        	}
    		
    		totalResult.put(srcKey, srcValue);
    	}
	}
 	
	public static int classJudge(String srcKey)
	{
		if(srcKey.lastIndexOf("초") == (srcKey.length()-1)|| (srcKey.lastIndexOf("초등학교") == (srcKey.length()-4) && srcKey.length() > 4))
		{
			return 1;
		}
		else if(srcKey.lastIndexOf("중") == (srcKey.length()-1) || (srcKey.lastIndexOf("중학교") == (srcKey.length()-3) && srcKey.length() > 3))
		{
			return 2;
		}
		else if(srcKey.lastIndexOf("고") == (srcKey.length()-1) || (srcKey.lastIndexOf("고등학교") == (srcKey.length()-4) && srcKey.length() > 4))
		{
			return 3;
		}
		else if(srcKey.lastIndexOf("대") == (srcKey.length()-1) || (srcKey.lastIndexOf("대학교") == (srcKey.length()-3) && srcKey.length() > 3))
		{
			return 4;
		}
		
		return -1;
	}
	
	public static void createTextFile()
	{
		/*
		　ㅇㅇ중학교\t192\n
		　ㅁㅁㅁ여중\t254\n
		　...
		 */
		String message = "";
		TreeMap<String,Integer> tm = new TreeMap<String,Integer>(totalResult);
		Iterator<String> iteratorKey = tm.keySet().iterator(); 
		while(iteratorKey.hasNext())
		{
		   String key = iteratorKey.next();
		   message += (key+"\t"+tm.get(key)+"\n");
		}
		
        File file = new File("result.txt");
        FileWriter writer = null;
        
        try {
            // 기존 파일의 내용에 이어서 쓰려면 true를, 기존 내용을 없애고 새로 쓰려면 false를 지정한다.
            writer = new FileWriter(file, false);
            writer.write(message);
            writer.flush();
             
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        } 
        
        System.out.println(tm);
		System.out.println("schoolCount = " + tm.size());
	}
	
	public static String StringReplace(String str)
	{
		String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str = str.replaceAll(match, " ");
        return str;
	}
	
	public static void insertData(String text)
	{
		learningData.add(text);
	}
	
	public static void insertData(String text, String keyword)
	{
		learningData.add(text);
		
		//학교이름 부분만 추출.
		String schoolName = text.substring(0, text.length() - keyword.length());
		
		//초, 중, 고, 대 이름 붙혀서 기억해 두자.
		//나중에 단어 찾을때 필요함.
		//형태소 최소화 하기 위해서(느림)
		String cutting = "";
		for(int i = 0; i < secondKeyword.length; i++)
		{
			boolean check = wordSexSearch(schoolName, false);
			if(check)
			{
				//하양여중, 하양여자중,
				cutting = schoolName.substring(0, schoolName.length() - 1);
				learningData.add(cutting + secondKeyword[i]);
			}
			
			String sum = schoolName + secondKeyword[i];
			
			if(!stopKeyWordSearch(sum))
			{
				learningData.add(sum);
			}
		}
	}
	
	
	public static boolean stopKeyWordSearch(String text)
	{
		//애매한 단어들이다..
		for(int stop = 0; stop < stopKeyword.length; stop++)
		{
			if(text.equals(stopKeyword[stop]))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static void analysisData(String text, String prvText, String matchingKeyword, boolean detail, int loop)
	{
		if(loop == 2) //두번쨰 검색,
		{
			//15자리 이상 들어오는 단어는 제외.
			if(text.length() > 15)
			{
				return;
			}
			
			//애매한 단어들이다..
			if(stopKeyWordSearch(text))
			{
				return;
			}
		 
			//급식이라는 단어가 있으면 스탑..
			if(text.indexOf("급식") != -1)
			{
				return;
			}
			
			//prvText 
			//서울중,  때문에.
			//뒤에있는단어까지 확인하자;
			//원래는 서울중앙여자중학교
			for(int j = 0; j < firstKeyword.length; j++)
			{
				int frontIndex = prvText.indexOf(firstKeyword[j]);
				if(frontIndex >= 0)
				{
					return;
				}
			} 
		}
		
		//두글자는 제외
		//먹고
		if(text.length() == 2) 
		{
			return;
		}
		
		//이미 저장되어 있던건 제외하자.
		//문수고
		if(learningData.contains(text) || learningData.contains(text.replaceAll(" ","")))
		{
			return;
		}
		
		//용문고등학교, 덕산중학교, 북중학교, 삼호중학교, 가재울중학교 //이런건 그냥 insert 하자
		if((text.length() - matchingKeyword.length() <= 3) && !detail && matchingKeyword.length() != 1) 
		{
			text = exceptionKeywordSearch(text);
			
			//text.length() - matchingKeyword.length()
			//저희초등학교가 나올때가 있다.
			//저희 <-- 제거
			//초등학교 만 남는 상황
			//제거하자.
			if(wordSexSearch(text, true) || text.length() - matchingKeyword.length() == 0)
			{
				//여자중학교
				return;
			}
			 
			if(text.length() > 0)
			{
				insertData(text, matchingKeyword);
			}
		}
		else
		{
			//그 외 처리.
			
			if(matchingKeyword.length() != 1)
			{
				//마동초등학교 -> 마동. 뒷부분 자르기.
				//자연어 처리할때 오차가 생기는 부분이 있다.
				text = text.substring(0, text.length() - matchingKeyword.length());
			}
			
			//예외처리.
			//저희초등학교 -> 초등학교.   
			//저희 제거.
			text = exceptionKeywordSearch(text);
 	
			Tagger tagger = new Tagger();
			
			//서울구로구개봉중학교
			//이런거 때문에,, 형태소 분석 사용!!
			List<Sentence> sentences = tagger.tag(text);
        	for(Sentence sent : sentences)
        	{ 
        		//
        		//detail = false -> 1번째 검색할때 쓰인다.
        		//detail = true  -> 2번째 검색할때 쓰인다.
        		//
        		if(!detail) 
    			{
        			// 첫번째 검색할 때 처리되는 부분.
        			firstAnalysisData(text, prvText, matchingKeyword, sent);
    			}
        		else
    			{
        			// 두번째 검색할 때 처리되는 부분.
        			secondAnalysisData(text, prvText, matchingKeyword, sent, tagger);
    			}
        	}
		}
	}
	
	public static void secondAnalysisData(String text, String prvText, String matchingKeyword, Sentence sent, Tagger tagger)
	{
		int specialSchoolIndex = 0;
		
		//특수목적학교는 바로 insert 하자..
		for(int sp = 0; sp < specialSchoolKeyword.length; sp++)
		{
			specialSchoolIndex = text.indexOf(specialSchoolKeyword[sp]);
			if(specialSchoolIndex != -1)
			{
				break;
			}
		}
		
		if(specialSchoolIndex != -1) //특수목적 학교일때!!
		{
			//특수목적 고등학교 일경우 실행된다.
			if(specialSchoolIndex != 0 && !wordSexSearch(text, true))
			{
				insertData(text);
			}
			else
			{
				//경화 여자EB고
				//대구 여자상업고
				//안산 디자인문화고  를 위해 
				List<Sentence> sentences_inner = tagger.tag(prvText);
	        	for(Sentence sent_inner : sentences_inner)
	        	{
	        		for(int j = 0; j <  sent_inner.size(); j++)
            		{
	        			Word word = sent_inner.get(j);
             			 
            			for(int m = 0; m < word.size(); m++)
            			{
            				Morpheme morph = word.get(m);
            				
            				//경화 여자EB고
            				//안산 디자인문화고
            				//대구 여자상업고
            				if((morph.getTag().toString().equals("NNP") || morph.getTag().toString().equals("NNG")))
                			{
            					insertData(prvText+text);
                			}
            			}
            		}
	        	}
			}
		}
		else
		{
			//
			//특수목적이 아닌,
			//일반학교일때 처리되는 부분.
			//
    		String chainStr = "";
    		boolean insertCheck = true;
    		boolean learningDataCheck = false;
			for(int j = 0; j <  sent.size(); j++)
    		{
    			Word word = sent.get(j);
    			             			
    			if(word.size() > 0)
    			{
        			Morpheme morph = word.get(0);
        			//MM은 이 목중, 때문에 삽입.
        			if(morph.getTag().toString().equals("NNG") || morph.getTag().toString().equals("NNB")  || morph.getTag().toString().equals("MM"))
        			{
    					chainStr += word.getSurface();
        			}
        			// || morph.getTag().toString().equals("NNP")// 백현중
    				 
    				if(morph.getTag().toString().equals("SN") || morph.getTag().toString().equals("SP"))
    				{
    					insertCheck = false;
    				}
    				//
    				//저장된 데이터가 있나 확인,
    				//있다면 분석 종료.
    				//
    				if(learningData.contains(morph.getSurface().toString()))
    				{
    					insertCheck = false;
    					learningDataCheck = true;
    					chainStr = "";
    					break;
    				}
    			}
    		}
			//
			//끝이 NNG 또는 NNB 일때 insert.
			//
			Word word = sent.get(sent.size()-1);

			Morpheme morph = word.get(word.size()-1);
			if(insertCheck && (morph.getTag().toString().equals("NNG") || morph.getTag().toString().equals("NNB")) && text.length() != 1)
			{            				
    			//고양시행신중 -> 고양 시행 신중
				//경기도화성시병점중 -> 경기도 화성시 병점 중
				for(int jj = 1; jj < text.length(); jj++)
				{
					String checkValue = text.substring(text.length()-jj, text.length());
					if(learningData.contains(checkValue))
    				{
						learningDataCheck = true;
						break;
    				}
				}
				//
				//ex. 정혜민 김정은 공나 영고
				//
				if(!learningDataCheck && chainStr.length() > 2)
				{
					if(chainStr.length() > 2 && !wordSexSearch(chainStr, true) && !frontSpecialSchoolSearch(chainStr))
    				{
    					insertData(chainStr);
    				}
				}
				else
				{
					return;
				}
			}
		}
	}
	
	public static void firstAnalysisData(String text, String prvText, String matchingKeyword, Sentence sent)
	{
		int specialSchoolIndex = 0;
		
		//특수목적학교는 바로 insert 하자..
		for(int sp = 0; sp < specialSchoolKeyword.length; sp++)
		{
			specialSchoolIndex = text.indexOf(specialSchoolKeyword[sp]);
			if(specialSchoolIndex != -1)
			{
				break;
			}
		}
		 
		if(specialSchoolIndex == -1)
		{
			if(matchingKeyword.length() == 4) //초등학교, 고등학교
    		{
				Word word = sent.get(sent.size()-1);
				boolean check = wordSexSearch(word.getSurface(), true);
				if(check)
    			{
					//서울구로구개봉중학교
					//이거 때문에,, 형태소 사용.
					//AA 여자 고등 학교
					String wordInner = "";
					
					wordInner += sent.get(sent.size()-2).getSurface();
    				wordInner += sent.get(sent.size()-1).getSurface();
    				
    				wordInner += matchingKeyword;
    				
    				if(wordInner.length() == (text.length()-1+matchingKeyword.length()))
    				{
    					//예 일 여자 고등학교
    					//예일여자고등학교
    					//원래는 일 여자 고등학교 인데,
    					insertData(text, matchingKeyword);
    				}
    				else
    				{
    					insertData(wordInner, matchingKeyword);
    				}	            				
    			}
    			else
    			{
    				//서울시립초등학교
    				String wordInner = "";

					wordInner += sent.get(sent.size()-1).getSurface();
					wordInner += matchingKeyword;

					if(wordInner.length() == (text.length()-1+matchingKeyword.length()))
    				{
    					//예 일 여자 고등학교
    					//예일여자중학교
    					//원래는 일 여자 중학교 인데,
    					insertData(text, matchingKeyword);
    				}
    				else
    				{
    					insertData(wordInner, matchingKeyword);
    				}
    			}
    			
    		}
    		else //중학교
    		{
    			Word word = sent.get(sent.size()-1);
    			//여자중학교 붙은곳
    			boolean check = wordSexSearch(word.getSurface(), true);
				if(check)
    			{
    				String wordInner = "";
    				//한내여자중학교 -> 한 내 여자
    				if(sent.get(sent.size()-2).getSurface().toString().length() == 1)
    				{
    					if(sent.size() - 3 >= 0)
    					{
    						wordInner += sent.get(sent.size()-3).getSurface();
    					}
    				}
    				wordInner += sent.get(sent.size()-2).getSurface();
					wordInner += sent.get(sent.size()-1).getSurface();
					wordInner += matchingKeyword;
					
    				if(wordInner.length() == (text.length()-1+matchingKeyword.length()))
    				{
    					insertData(text, matchingKeyword);
    				}
    				else
    				{
    					insertData(wordInner, matchingKeyword);
    				}
    			}
    			else
    			{
    				//서울시립중학교
    				String wordInner = "";
     				
    				//서곶중학교 -> 서 곶
    				if(sent.get(sent.size()-1).getSurface().toString().length() == 1)
    				{
    					if(sent.size() - 2 >= 0)
    					{
    						wordInner += sent.get(sent.size()-2).getSurface();
    					}
    				}
    				wordInner += sent.get(sent.size()-1).getSurface();
    				wordInner += matchingKeyword;
    				
    				if(wordInner.length() == (text.length()-1+matchingKeyword.length()))
    				{
    					insertData(text, matchingKeyword);
    				}
    				else
    				{
    					insertData(wordInner, matchingKeyword);
    				}
    			}
    		}
		}
		else
		{
			//특수목적학교는 바로 insert 하자..
			boolean check = wordSexSearch(text, true);
			
			if(!check)
			{
				insertData(text+matchingKeyword, matchingKeyword);
			}
			
			//대학교 추출을 위해서  
			//ex. 경북대학교사범대학부설중학교
			for(int at = 0; at < attachSchoolKeyword.length; at++)
			{
				int index = text.indexOf(attachSchoolKeyword[at]);
				if(index != -1)
				{
					text = text.substring(0, index + attachSchoolKeyword[at].length());
					insertData(text, attachSchoolKeyword[at]);
					break;
				}
			}
		}
	}
	
	public static void keywordSearch(String text, String[] keyword, boolean detail, int loopCount)
	{
		String[] textArr = text.split(" ");
		
		if(textArr.length > 0)
		{ 
			for(int i = 0; i < textArr.length; i++)
			{  
				if(i > 0)
				{
					keywordSearchInner(textArr[i], textArr[i-1], keyword, detail, loopCount);
				}
				else
				{
					keywordSearchInner(textArr[i], textArr[i], keyword, detail, loopCount);
				}
			}
		}
	}
	
	public static void keywordSearchInner(String text, String prvText, String[] keyword, boolean detail, int loop)
	{
		for(int j = 0; j < keyword.length; j++)
		{
			int frontIndex = text.indexOf(keyword[j]);
			if(frontIndex >= 0)
			{
				String cuttingStr = text.substring(0, frontIndex + keyword[j].length());
				
				if(!naturalSearch.contains(cuttingStr))
				{
					naturalSearch.add(cuttingStr);	

					analysisData(cuttingStr, prvText, keyword[j], detail, loop);
				}
				
				String tailStr = text.substring(frontIndex + keyword[j].length(), text.length());
 				if(tailStr.length() >= keyword[j].length())
				{
					keywordSearch(tailStr, keyword, detail, loop);
				}
				
				break;
			}
		}
	}
	
	public static boolean wordSexSearch(String text, boolean front)
	{
		for(int sx = 0; sx < sexKeyword.length; sx++)
		{
			if(front)
			{
				int index = text.indexOf(sexKeyword[sx]);
				if(index == 0)
				{
					//여자상업고등학교 제외시키기 위해서..
					//앞글자에 여자, 남자 붙는 학교 제외시키기 위해서
					return true;
				}
			}
			else
			{
				int index = text.lastIndexOf(sexKeyword[sx]);
				if(index != -1)
				{
					//여자상업고등학교 제외시키기 위해서..
					//앞글자에 여자, 남자 붙는 학교 제외시키기 위해서
					return true;
				}
			}
			
		}
		return false;
	}
	
	public static boolean frontSpecialSchoolSearch(String text)
	{
		for(int sx = 0; sx < specialSchoolKeyword.length; sx++)
		{
			int index = text.indexOf(specialSchoolKeyword[sx]);
			if(index == 0)
			{
				//특수고등학교가 맨앞에 붙는거 찾기..
				return true;
			}
			
		}
		return false;
	}
	
	public static String exceptionKeywordSearch(String text)
	{ 
		for(int ex = 0; ex < exceptionKeyword.length; ex++)
		{
			text = text.replaceAll(exceptionKeyword[ex], "");
		}
		return text;
	}
}
