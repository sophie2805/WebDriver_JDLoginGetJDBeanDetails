package pkg_JDLogin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import java.util.List;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class JDLogin {
    private WebDriver dr;
    private String url;
    private String account;
    private String pwd;
    private String myJDXPATH;
    private String myBeanXPATH;
    private String datesXPATH;
    private String transacDetailsXPATH;
    private String transacNameXPATH;
    private String pageNextXPATH;
    private class JDBeanDetails{
        private List dateDetail;
        private List transacDetail;
        private List transacName;
    }

    public JDLogin(String url, String account, String pwd,String myJDXPATH, String myBeanXPATH,
                   String datesXPATH, String transacDetailsXPATH, String transacNameXPATH, String pageNextXPATH){
        this.url = url;
        this.dr = new FirefoxDriver();
        this.dr.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        this.account = account;
        this.pwd = pwd;
        this.myJDXPATH = myJDXPATH;
        this.myBeanXPATH = myBeanXPATH;
        this.datesXPATH = datesXPATH;
        this.transacDetailsXPATH = transacDetailsXPATH;
        this.transacNameXPATH = transacNameXPATH;
        this.pageNextXPATH = pageNextXPATH;
    }

    public void login() throws Exception{
        dr.get(url);
        dr.findElement(By.id("loginname")).clear();
        dr.findElement(By.id("loginname")).sendKeys(account);
        dr.findElement(By.id("nloginpwd")).clear();
        dr.findElement(By.id("nloginpwd")).sendKeys(pwd);
        dr.findElement(By.id("loginsubmit")).click();
        //return dr;
    }

    public void JDBeanPage(){
        WebElement dropDown = dr.findElement(By.xpath(myJDXPATH));
        Actions act = new Actions(dr);
        act.click(dropDown).perform();
        dr.get(dr.findElement(By.xpath(myBeanXPATH)).getAttribute("href"));
    }

    public List getDatesDetail(WebDriver dr){
        List<WebElement> e = dr.findElements(By.xpath(datesXPATH));
        List result = new ArrayList<String>();
        for (int i = 0; i < e.size(); i ++){
            result.add(e.get(i).getText());
        }

        return result;
    }

    public List getTransacDetail(WebDriver dr){
        List<WebElement> e = dr.findElements(By.xpath(transacDetailsXPATH));
        List result = new ArrayList<String>();
        for (int i = 0; i < e.size(); i ++){
            result.add(e.get(i).getText());
        }

        return result;
    }

    public List getTransacName(WebDriver dr){
        List<WebElement> e = dr.findElements(By.xpath(transacNameXPATH));
        List result = new ArrayList<String>();
        for (int i = 0; i < e.size(); i ++){
            result.add(e.get(i).getText());
        }

        return result;
    }

    public void getJDBeanDetails(){

        JDBeanDetails beanDetails = new JDBeanDetails();
        //dates that have transactions
        beanDetails.dateDetail = getDatesDetail(dr);
        //gain or lost JDBean
        beanDetails.transacDetail = getTransacDetail(dr);
        //name of transac
        beanDetails.transacName = getTransacName(dr);

        Actions act = new Actions(dr);

        //get pages size
        List<WebElement> pageHref = dr.findElements(By.xpath("//div[@class='pagin fr']/a[not(@class) and @href]"));
        act.moveToElement(pageHref.get(0));
        act.click().perform();
        //System.out.print(dr.getPageSource());

        for (int i = 1; i < pageHref.size(); i ++) {
            WebElement pageIcon = dr.findElement(By.xpath(pageNextXPATH));
            act.moveToElement(pageIcon);
            act.click().perform();

            //date
            beanDetails.dateDetail.addAll(getDatesDetail(dr));

            //gain or lost
            beanDetails.transacDetail.addAll(getTransacDetail(dr));

            //name of transac
            beanDetails.transacName.addAll(getTransacName(dr));

        }

        maskItemNumber(beanDetails);
        //return void;

        for(int i = 0; i < beanDetails.transacName.size(); i ++){
            System.out.println(beanDetails.dateDetail.get(i)+"\t\t\t"+beanDetails.transacDetail.get(i)+"\t\t\t\t\t"+beanDetails.transacName.get(i));
        }
    }

    public void maskItemNumber(JDBeanDetails beanDetails){
        StringBuffer s ;
        for (int i = 0; i < beanDetails.transacName.size(); i ++){
            if(beanDetails.transacName.get(i).toString().contains("商品")){
                s = new StringBuffer(beanDetails.transacName.get(i).toString());
                for(int j = 0; j < s.length(); j ++){
                    if (Character.isDigit(s.charAt(j)))
                        s.setCharAt(j,'*');
                }
                beanDetails.transacName.set(i,s.toString());
            }
        }
    }


    public void tearDown() throws Exception{
        dr.quit();
    }

    public static void main(String[] args) throws Exception {
        String url = "https://passport.jd.com/uc/login?ltype=logout";
        String account = "***";
        String pwd = "***";
        String myJDXPATH = "//li[@id='ttbar-myjd']/div[1]/i[@class='ci-right']/s";
        String myBeanXPATH = "//li[@id='ttbar-myjd']/div[2]/div[@class='otherlist']/div[@class='fore2']/div[2]/a";
        String datesXPATH = "//span[@class='ftx03']";
        String transacDetailsXPATH = "//table[@class='tb-void']/tbody/tr/td[2]/span";
        String transacNameXPATH = "//table[@class='tb-void']/tbody/tr/td[3]";
        String pageNextXPATH = "//div[@class='pagin fr']/a[@class='current']/following-sibling::a[1]";
        JDLogin jd = new JDLogin(url,  account,  pwd, myJDXPATH,  myBeanXPATH, datesXPATH,  transacDetailsXPATH,  transacNameXPATH,  pageNextXPATH);
        jd.login();
        jd.JDBeanPage();
        jd.getJDBeanDetails();
        jd.tearDown();
    }
}
