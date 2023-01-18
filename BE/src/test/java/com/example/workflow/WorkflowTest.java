package com.example.workflow;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.xml.impl.type.ModelElementTypeBuilderImpl;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.example.workflow.dataModel.JsonData;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WorkflowTest extends AbstractProcessEngineRuleTest {

  private static final String NODE_FILED_NAME = "name";
  private static final String NODE_FILED_ATTR = "attribute";
  private static final String NODE_FILED_CONTENT = "content";

  public static JSONObject getJSON(Node node, boolean withoutNamespaces) throws JSONException {
      JSONObject jsonObject = new JSONObject();
      jsonObject.putOpt(NODE_FILED_NAME, namespace(node.getNodeName(), withoutNamespaces));

      if (node.hasAttributes()) {
          JSONObject jsonAttr = new JSONObject();
          NamedNodeMap attr = node.getAttributes();
          int attrLenth = attr.getLength();
          for (int i = 0; i < attrLenth; i++) {
              Node attrItem = attr.item(i);
              String name = namespace(attrItem.getNodeName(), withoutNamespaces);
              String value = attrItem.getNodeValue();
              jsonAttr.putOpt(name, value);
          }
          jsonObject.putOpt(NODE_FILED_ATTR, jsonAttr);
      }

      if (node.hasChildNodes()) {
          NodeList children = node.getChildNodes();
          int childrenCount = children.getLength();

          if (childrenCount == 1) {
              Node item = children.item(0);
              int itemType = item.getNodeType();
              if (itemType == Node.TEXT_NODE) {
                  jsonObject.putOpt(NODE_FILED_CONTENT, item.getNodeValue());
                  return jsonObject;
              }
          }

          for (int i = 0; i < childrenCount; i++) {
              Node item = children.item(i);
              int itemType = item.getNodeType();
              if (itemType == Node.DOCUMENT_NODE || itemType == Node.ELEMENT_NODE) {
                  JSONObject jsonItem = getJSON(item, withoutNamespaces);
                  if (jsonItem != null) {
                      String name = jsonItem.optString(NODE_FILED_NAME);

                      if (!jsonObject.has(NODE_FILED_CONTENT)) {
                          jsonObject.putOpt(NODE_FILED_CONTENT, new JSONObject());
                      }
                      JSONObject jsonContent = jsonObject.optJSONObject(NODE_FILED_CONTENT);

                      Object jsonByName = jsonContent.opt(name);
                      if (jsonByName == null) {
                          jsonContent.putOpt(name, jsonItem);
                      } else if (jsonByName instanceof JSONArray) {
                          ((JSONArray) jsonByName).put(jsonItem);
                      } else {
                          JSONArray arr = new JSONArray();
                          arr.put(jsonByName);
                          arr.put(jsonItem);
                          jsonContent.putOpt(name, arr);
                      }
                  }
              }
          }
      } else {
          jsonObject.putOpt(NODE_FILED_CONTENT, node.getNodeValue());
      }

      return jsonObject;
  }

  private static String namespace(String nodeName, boolean withoutNamespaces) {
    return nodeName;
  }

  private Node findNodeByTaskDefinitionId(String taskDefinitionKey, String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(false);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(new InputSource(new StringReader(xml)));
    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr = xpath.compile("//*[@id='" + taskDefinitionKey + "']");

    return (Node) expr.evaluate(doc, XPathConstants.NODE);
  }


  @Autowired
  public RuntimeService runtimeService;
  
  @Ignore
  @Test
  public void shouldExecuteHappyPath() {
    // given
    String processDefinitionKey = "camundaTest-process";

    // when
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);

    // then
    assertThat(processInstance).isStarted()
        .task()
        .hasDefinitionKey("say-hello")
        .hasCandidateUser("demo")
        .isNotAssigned();
  }

  @Test
  public void shouldParseXml() {

    // examples : https://github.com/camunda/camunda-bpm-examples/blob/master/bpmn-model-api/parse-bpmn/src/test/java/org/camunda/bpm/example/modelapi/ParseBpmnTest.java

    BpmnModelInstance modelInst;
        try {
        // File file = new File(ModelModifier.class.getClassLoader().getResource("process1.bpmn").toURI());

        //"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:camunda=\"http://camunda.org/schema/1.0/bpmn\" id=\"Definitions_0fr9mxs\" targetNamespace=\"http://bpmn.io/schema/bpmn\" exporter=\"Camunda Modeler\" exporterVersion=\"5.5.1\">\n  <bpmn:process id=\"camundaTest-process\" name=\"MyFirstProcess\" isExecutable=\"true\">\n    <bpmn:startEvent id=\"StartEvent_1\">\n      <bpmn:outgoing>SequenceFlow_1fp17al</bpmn:outgoing>\n    </bpmn:startEvent>\n    <bpmn:sequenceFlow id=\"SequenceFlow_1fp17al\" sourceRef=\"StartEvent_1\" targetRef=\"say-hello\" />\n    <bpmn:endEvent id=\"EndEvent_0x6ir2l\">\n      <bpmn:incoming>Flow_0gcq2ys</bpmn:incoming>\n    </bpmn:endEvent>\n    <bpmn:sequenceFlow id=\"SequenceFlow_16gzt2m\" sourceRef=\"say-hello\" targetRef=\"Activity_1xcl7s1\" />\n    <bpmn:userTask id=\"say-hello\" name=\"SomeForm\">\n      <bpmn:extensionElements>\n        <camunda:formData>\n          <camunda:formField id=\"Meno\" label=\"Meno\" type=\"string\">\n            <camunda:properties>\n              <camunda:property id=\"Validator\" value=\"SomeValidator\" />\n            </camunda:properties>\n            <camunda:validation />\n          </camunda:formField>\n          <camunda:formField id=\"Priezvysko\" label=\"Priezvysko\" type=\"string\" defaultValue=\"&#34;&#34;\" />\n        </camunda:formData>\n        <camunda:inputOutput>\n          <camunda:outputParameter name=\"UserName\">${Meno}+${Priezvysko}</camunda:outputParameter>\n        </camunda:inputOutput>\n      </bpmn:extensionElements>\n      <bpmn:incoming>SequenceFlow_1fp17al</bpmn:incoming>\n      <bpmn:outgoing>SequenceFlow_16gzt2m</bpmn:outgoing>\n    </bpmn:userTask>\n    <bpmn:sequenceFlow id=\"Flow_0gcq2ys\" sourceRef=\"Activity_1xcl7s1\" targetRef=\"EndEvent_0x6ir2l\" />\n    <bpmn:sendTask id=\"Activity_1xcl7s1\" name=\"send mail\" camunda:delegateExpression=\"${sendMail}\">\n      <bpmn:extensionElements />\n      <bpmn:incoming>SequenceFlow_16gzt2m</bpmn:incoming>\n      <bpmn:outgoing>Flow_0gcq2ys</bpmn:outgoing>\n    </bpmn:sendTask>\n  </bpmn:process>\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\n    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"camundaTest-process\">\n      <bpmndi:BPMNShape id=\"_BPMNShape_StartEvent_2\" bpmnElement=\"StartEvent_1\">\n        <dc:Bounds x=\"179\" y=\"99\" width=\"36\" height=\"36\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_06wlc8e_di\" bpmnElement=\"say-hello\">\n        <dc:Bounds x=\"270\" y=\"77\" width=\"100\" height=\"80\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"EndEvent_0x6ir2l_di\" bpmnElement=\"EndEvent_0x6ir2l\">\n        <dc:Bounds x=\"722\" y=\"112\" width=\"36\" height=\"36\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_15kl2hq_di\" bpmnElement=\"Activity_1xcl7s1\">\n        <dc:Bounds x=\"470\" y=\"77\" width=\"100\" height=\"80\" />\n        <bpmndi:BPMNLabel />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNEdge id=\"SequenceFlow_1fp17al_di\" bpmnElement=\"SequenceFlow_1fp17al\">\n        <di:waypoint x=\"215\" y=\"117\" />\n        <di:waypoint x=\"270\" y=\"117\" />\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"SequenceFlow_16gzt2m_di\" bpmnElement=\"SequenceFlow_16gzt2m\">\n        <di:waypoint x=\"370\" y=\"117\" />\n        <di:waypoint x=\"470\" y=\"117\" />\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"Flow_0gcq2ys_di\" bpmnElement=\"Flow_0gcq2ys\">\n        <di:waypoint x=\"570\" y=\"117\" />\n        <di:waypoint x=\"646\" y=\"117\" />\n        <di:waypoint x=\"646\" y=\"130\" />\n        <di:waypoint x=\"722\" y=\"130\" />\n      </bpmndi:BPMNEdge>\n    </bpmndi:BPMNPlane>\n  </bpmndi:BPMNDiagram>\n</bpmn:definitions>\n"
            File file = new File("D:\\alfabase\\CamundaTest\\src\\main\\resources\\process.bpmn");
          modelInst = Bpmn.readModelFromFile(file); 
            // modelInst = Bpmn.createProcess()
            //         .name("Twitter QA")
            //         .executable()
            //         .startEvent()
            //         .userTask().id("ApproveTweet").name("Approve Tweet")
            //         .exclusiveGateway().id("isApproved").name("Approved?")
            //         .condition("approved", "#{approved}")
            //         .serviceTask().id("sendTweet").name("Send tweet")
            //         .endEvent().name("Tweet sent")
            //         .moveToLastGateway()
            //         // done();
            //         // Gateway gateway = modelInst.getModelElementById("isApproved");
            //         // gateway.builder()
            //         .condition("Not approved", "#{!approved}")
            //         .serviceTask().name("Send Rejection")
            //         .endEvent().name("Tweet rejected").done();

            // log.info("Flow Elements - Name : Id : Type Name");
            var lst = modelInst.getModelElementsByType(UserTask.class);
            var task = lst.stream().findFirst();
            if(task.isPresent()){
              var extEls = task.get().getChildElementsByType(ExtensionElements.class);
              var fds = extEls.stream().findFirst().get().getChildElementsByType(CamundaFormData.class);
              var fields = fds.stream().findFirst().get().getCamundaFormFields();
              fields.forEach((f) -> { 
                var props = f.getCamundaProperties();
                var dts = "";
                if(props != null){
                  dts = props.getCamundaProperties().stream().map(p -> p.getCamundaId()).collect(Collectors.joining(","));
                }
                System.out.println(f.getCamundaId() + ", " + dts);
              });
            }

            lst.forEach(e -> System.out.println(e.getName()+ e.getId()+e.getElementType().getTypeName()));

            //Bpmn.writeModelToFile(file, modelInst);
//        file.createNewFile("/tmp/testDiagram2.bpmn")

        } catch (Exception e) {
            e.printStackTrace();
        }
  }

  @Test
  public void shouldParseXm1() {
    BpmnModelInstance modelInst;
        try {
          var str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:camunda=\"http://camunda.org/schema/1.0/bpmn\" id=\"Definitions_0fr9mxs\" targetNamespace=\"http://bpmn.io/schema/bpmn\" exporter=\"Camunda Modeler\" exporterVersion=\"5.5.1\">\n  <bpmn:process id=\"camundaTest-process\" name=\"MyFirstProcess\" isExecutable=\"true\">\n    <bpmn:startEvent id=\"StartEvent_1\">\n      <bpmn:outgoing>SequenceFlow_1fp17al</bpmn:outgoing>\n    </bpmn:startEvent>\n    <bpmn:sequenceFlow id=\"SequenceFlow_1fp17al\" sourceRef=\"StartEvent_1\" targetRef=\"say-hello\" />\n    <bpmn:endEvent id=\"EndEvent_0x6ir2l\">\n      <bpmn:incoming>Flow_0gcq2ys</bpmn:incoming>\n    </bpmn:endEvent>\n    <bpmn:sequenceFlow id=\"SequenceFlow_16gzt2m\" sourceRef=\"say-hello\" targetRef=\"Activity_1xcl7s1\" />\n    <bpmn:userTask id=\"say-hello\" name=\"SomeForm\">\n      <bpmn:extensionElements>\n        <camunda:formData>\n          <camunda:formField id=\"Meno\" label=\"Meno\" type=\"string\">\n            <camunda:properties>\n              <camunda:property id=\"Validator\" value=\"SomeValidator\" />\n            </camunda:properties>\n            <camunda:validation />\n          </camunda:formField>\n          <camunda:formField id=\"Priezvysko\" label=\"Priezvysko\" type=\"string\" defaultValue=\"&#34;&#34;\" />\n        </camunda:formData>\n        <camunda:inputOutput>\n          <camunda:outputParameter name=\"UserName\">${Meno}+${Priezvysko}</camunda:outputParameter>\n        </camunda:inputOutput>\n      </bpmn:extensionElements>\n      <bpmn:incoming>SequenceFlow_1fp17al</bpmn:incoming>\n      <bpmn:outgoing>SequenceFlow_16gzt2m</bpmn:outgoing>\n    </bpmn:userTask>\n    <bpmn:sequenceFlow id=\"Flow_0gcq2ys\" sourceRef=\"Activity_1xcl7s1\" targetRef=\"EndEvent_0x6ir2l\" />\n    <bpmn:sendTask id=\"Activity_1xcl7s1\" name=\"send mail\" camunda:delegateExpression=\"${sendMail}\">\n      <bpmn:extensionElements />\n      <bpmn:incoming>SequenceFlow_16gzt2m</bpmn:incoming>\n      <bpmn:outgoing>Flow_0gcq2ys</bpmn:outgoing>\n    </bpmn:sendTask>\n  </bpmn:process>\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\n    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"camundaTest-process\">\n      <bpmndi:BPMNShape id=\"_BPMNShape_StartEvent_2\" bpmnElement=\"StartEvent_1\">\n        <dc:Bounds x=\"179\" y=\"99\" width=\"36\" height=\"36\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_06wlc8e_di\" bpmnElement=\"say-hello\">\n        <dc:Bounds x=\"270\" y=\"77\" width=\"100\" height=\"80\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"EndEvent_0x6ir2l_di\" bpmnElement=\"EndEvent_0x6ir2l\">\n        <dc:Bounds x=\"722\" y=\"112\" width=\"36\" height=\"36\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_15kl2hq_di\" bpmnElement=\"Activity_1xcl7s1\">\n        <dc:Bounds x=\"470\" y=\"77\" width=\"100\" height=\"80\" />\n        <bpmndi:BPMNLabel />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNEdge id=\"SequenceFlow_1fp17al_di\" bpmnElement=\"SequenceFlow_1fp17al\">\n        <di:waypoint x=\"215\" y=\"117\" />\n        <di:waypoint x=\"270\" y=\"117\" />\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"SequenceFlow_16gzt2m_di\" bpmnElement=\"SequenceFlow_16gzt2m\">\n        <di:waypoint x=\"370\" y=\"117\" />\n        <di:waypoint x=\"470\" y=\"117\" />\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"Flow_0gcq2ys_di\" bpmnElement=\"Flow_0gcq2ys\">\n        <di:waypoint x=\"570\" y=\"117\" />\n        <di:waypoint x=\"646\" y=\"117\" />\n        <di:waypoint x=\"646\" y=\"130\" />\n        <di:waypoint x=\"722\" y=\"130\" />\n      </bpmndi:BPMNEdge>\n    </bpmndi:BPMNPlane>\n  </bpmndi:BPMNDiagram>\n</bpmn:definitions>\n";
          Node formNode = findNodeByTaskDefinitionId("say-hello", str);
          String json = getJSON(formNode, false).toString();   
          System.out.println(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
  }

  @Test
  public void shouldParseXmlFromRest() {
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:8080/engine-rest/process-definition/camundaTest-process:2:c793f2d8-91c5-11ed-aa10-106fd9dce29f/xml";
    JsonData data = restTemplate.getForObject(url, JsonData.class);
    System.out.println(data);

    BpmnModelInstance modelInst = Bpmn.readModelFromStream(new ByteArrayInputStream(data.getBpmn20Xml().getBytes()));

    var lst = modelInst.getModelElementsByType(UserTask.class);
    var task = lst.stream().findFirst();
    if(task.isPresent()){
      var extEls = task.get().getChildElementsByType(ExtensionElements.class);
      var fds = extEls.stream().findFirst().get().getChildElementsByType(CamundaFormData.class);
      var fields = fds.stream().findFirst().get().getCamundaFormFields();
      fields.forEach((f) -> { 
        var props = f.getCamundaProperties();
        var dts = "";
        if(props != null){
          dts = props.getCamundaProperties().stream().map(p -> p.getCamundaId()).collect(Collectors.joining(","));
        }
        System.out.println(f.getCamundaId() + ", " + dts);
      });
    }

    lst.forEach(e -> System.out.println(e.getName()+ e.getId()+e.getElementType().getTypeName()));
  }

}
