package com.lucidworks.searchhub.connectors.mail.handler;


import com.lucidworks.apollo.common.pipeline.PipelineDocument;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 *
 *
 **/
public class MailTest {

  //http://asfmail.lucidworks.io/mail_files/cassandra-user/201605.mbox/raw/%3C4924E32E-FCDC-4F7B-9EC9-C454F9E12F2A@venarc.com%3E
  //http://asfmail.lucidworks.io/mail_files/lucene-dev/201606.mbox/raw/%3CJIRA.12974273.1464711795000.820.1464773399553@Atlassian.JIRA%3E
  @Test
  public void testEvictMessage() throws Exception {
    Session session = Session.getDefaultInstance(new Properties());
    MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(Files.readAllBytes(Paths.get(getClass().getResource("/cassandra-evict-msg.txt").toURI()))));
    Mail mail = new Mail(message, "http://asfmail.lucidworks.io/mail_files/cassandra-user/201605.mbox/%3C284482193.749659.1464463496825.JavaMail.yahoo@mail.yahoo.com%3E");
    String displayContent = mail.getDisplayContent();
    Assert.assertNotNull("display content is null", displayContent);
    Assert.assertTrue(displayContent.trim().startsWith("Hi,\n" +
            "We are using C* 2.0.x ") );

  }

  @Test
  public void testReplyMessage() throws Exception {
    Session session = Session.getDefaultInstance(new Properties());
    MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(Files.readAllBytes(Paths.get(getClass().getResource("/cassandra-debian-msg.txt").toURI()))));
    Mail mail = new Mail(message, "http://asfmail.lucidworks.io/mail_files/cassandra-user/201605.mbox/raw/%3C4924E32E-FCDC-4F7B-9EC9-C454F9E12F2A@venarc.com%3E");
    String displayContent = mail.getDisplayContent();
    Assert.assertNotNull("display content is null", displayContent);
    Assert.assertTrue(displayContent.trim().startsWith("OK to make things even more confusing,") );
    Assert.assertEquals(-1, displayContent.trim().indexOf(">"));

  }

  @Test
  public void testTextOnlyMessage() throws Exception {
    Session session = Session.getDefaultInstance(new Properties());
    MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(Files.readAllBytes(Paths.get(getClass().getResource("/text-only.txt").toURI()))));
    Mail mail = new Mail(message, "http://foo.com/text-only.txt");
    String displayContent = mail.getDisplayContent();
    Assert.assertNotNull("display content is null", displayContent);
    Assert.assertTrue(displayContent.trim().contains("I've updated the patch and the returned collection is now a view."));

  }

  @Test
  public void testHTMLOnlyMessage() throws Exception {
    Session session = Session.getDefaultInstance(new Properties());
    MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(Files.readAllBytes(Paths.get(getClass().getResource("/html-only.txt").toURI()))));
    Mail mail = new Mail(message, "http://foo.com/text-only.txt");
    String displayContent = mail.getDisplayContent();
    Assert.assertNotNull("display content is null", displayContent);
    Assert.assertFalse(displayContent.trim().isEmpty());

  }

  @Test
  public void testDates() throws Exception {
    MimeMailParser parser = new MimeMailParser();
    PipelineDocument doc = new PipelineDocument("http://asfmail.lucidworks.io/mail_files/spark-user/201406.mbox/raw/%3C1403685756003-8246.post@n3.nabble.com%3E");
    doc.addField(MimeMailParser.RAW_CONTENT, SPARK.getBytes());
    PipelineDocument newDoc = parser.parse(doc);
    String dateStr = (String) newDoc.getFieldValues(MimeMailParser.FIELD_SENT_DATE).get(0);
    Assert.assertEquals("2014-06-25T08:42:36Z", dateStr);
  }

  public static final String SPARK = "From user-return-10156-apmail-spark-user-archive=spark.apache.org@spark.apache.org  Wed Jun 25 08:43:03 2014\n" +
          "Return-Path: <user-return-10156-apmail-spark-user-archive=spark.apache.org@spark.apache.org>\n" +
          "X-Original-To: apmail-spark-user-archive@minotaur.apache.org\n" +
          "Delivered-To: apmail-spark-user-archive@minotaur.apache.org\n" +
          "Received: from mail.apache.org (hermes.apache.org [140.211.11.3])\n" +
          "\tby minotaur.apache.org (Postfix) with SMTP id 5E0DC11751\n" +
          "\tfor <apmail-spark-user-archive@minotaur.apache.org>; Wed, 25 Jun 2014 08:43:03 +0000 (UTC)\n" +
          "Received: (qmail 25059 invoked by uid 500); 25 Jun 2014 08:43:02 -0000\n" +
          "Delivered-To: apmail-spark-user-archive@spark.apache.org\n" +
          "Received: (qmail 25013 invoked by uid 500); 25 Jun 2014 08:43:02 -0000\n" +
          "Mailing-List: contact user-help@spark.apache.org; run by ezmlm\n" +
          "Precedence: bulk\n" +
          "List-Help: <mailto:user-help@spark.apache.org>\n" +
          "List-Unsubscribe: <mailto:user-unsubscribe@spark.apache.org>\n" +
          "List-Post: <mailto:user@spark.apache.org>\n" +
          "List-Id: <user.spark.apache.org>\n" +
          "Reply-To: user@spark.apache.org\n" +
          "Delivered-To: mailing list user@spark.apache.org\n" +
          "Received: (qmail 25003 invoked by uid 99); 25 Jun 2014 08:43:02 -0000\n" +
          "Received: from athena.apache.org (HELO athena.apache.org) (140.211.11.136)\n" +
          "    by apache.org (qpsmtpd/0.29) with ESMTP; Wed, 25 Jun 2014 08:43:02 +0000\n" +
          "X-ASF-Spam-Status: No, hits=2.3 required=5.0\n" +
          "\ttests=SPF_SOFTFAIL,URI_HEX\n" +
          "X-Spam-Check-By: apache.org\n" +
          "Received-SPF: softfail (athena.apache.org: transitioning domain of pc175@uow.edu.au does not designate 216.139.236.26 as permitted sender)\n" +
          "Received: from [216.139.236.26] (HELO sam.nabble.com) (216.139.236.26)\n" +
          "    by apache.org (qpsmtpd/0.29) with ESMTP; Wed, 25 Jun 2014 08:42:57 +0000\n" +
          "Received: from ben.nabble.com ([192.168.236.152])\n" +
          "\tby sam.nabble.com with esmtp (Exim 4.72)\n" +
          "\t(envelope-from <pc175@uow.edu.au>)\n" +
          "\tid 1Wzimq-0002Qq-0P\n" +
          "\tfor user@spark.incubator.apache.org; Wed, 25 Jun 2014 01:42:36 -0700\n" +
          "Date: Wed, 25 Jun 2014 01:42:36 -0700 (PDT)\n" +
          "From: Peng Cheng <pc175@uow.edu.au>\n" +
          "To: user@spark.incubator.apache.org\n" +
          "Message-ID: <1403685756003-8246.post@n3.nabble.com>\n" +
          "In-Reply-To: <1403653195980-8227.post@n3.nabble.com>\n" +
          "References: <1403648908450-8203.post@n3.nabble.com> <1403649012458-8204.post@n3.nabble.com> <1403653195980-8227.post@n3.nabble.com>\n" +
          "Subject: Re: Spark slave fail to start with wierd error information\n" +
          "MIME-Version: 1.0\n" +
          "Content-Type: text/plain; charset=us-ascii\n" +
          "Content-Transfer-Encoding: 7bit\n" +
          "X-Virus-Checked: Checked by ClamAV on apache.org\n" +
          "\n" +
          "Sorry I just realize that start-slave is for a different task. Please close\n" +
          "this\n" +
          "\n" +
          "\n" +
          "\n" +
          "--\n" +
          "View this message in context: http://apache-spark-user-list.1001560.n3.nabble.com/Spark-slave-fail-to-start-with-wierd-error-information-tp8203p8246.html\n" +
          "Sent from the Apache Spark User List mailing list archive at Nabble.com.\n";
}
