package com.example.demo.controller;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Sparkql {

    @GetMapping("")
    public String index(Model springModel) {
        org.apache.jena.rdf.model.Model jenaModel = ModelFactory.createDefaultModel(); // Sử dụng JenaModel

        // Đọc dữ liệu từ tệp RDF hoặc URL RDF vào mô hình
        jenaModel.read("data/data.owl");

        // Câu truy vấn SPARQL bằng tiếng Việt
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + //
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + //
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + //
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n" + //
                "PREFIX O: <http://www.semanticweb.org/admin/ontologies/2024/1/untitled-ontology-5#>" +
                "SELECT ?subject ?predicate ?value " +
                "WHERE { " +
                "   ?subject rdf:type O:Đồ_thị .\r\n" + //
                "    ?subject ?predicate ?value .\r\n" + //
                "    FILTER isLiteral(?value) " + "}";

        try (QueryExecution qexec = QueryExecutionFactory.create(queryString, jenaModel)) {
            // Lấy kết quả
            ResultSet results = qexec.execSelect();

            // Xử lý kết quả
            StringBuilder resultBuilder = new StringBuilder(); // Sử dụng StringBuilder để tạo nội dung kết quả

            //while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                // Lấy giá trị của biến "?subject" trong kết quả
                Resource subjectResource = soln.getResource("subject");

                if (subjectResource != null) {
                    String subjectURI = subjectResource.getLocalName();
                    resultBuilder.append("URI của subject: ").append(subjectURI).append("\n");

                    // Lấy các thuộc tính và giá trị của individual
                    StmtIterator properties = subjectResource.listProperties();

                    while (properties.hasNext()) {
                        Statement stmt = properties.nextStatement();
                        String predicate = stmt.getPredicate().getLocalName();
                        RDFNode object = stmt.getObject();

                        if (object.isLiteral()) {
                            String value = object.asLiteral().getString();
                            resultBuilder.append("Thuộc tính: ").append(predicate).append(", Giá trị: ").append(value)
                                    .append("\n");
                        }
                    }
                } else {
                    resultBuilder.append("Không có giá trị cho biến subject.").append("\n");
                }
            //}

            String result = resultBuilder.toString(); // Chuyển StringBuilder thành String

            // Truyền kết quả vào model để sử dụng trong HTML
            springModel.addAttribute("result", result);

            // Trả về template HTML để hiển thị kết quả
            return "index";
        }
    }
}
