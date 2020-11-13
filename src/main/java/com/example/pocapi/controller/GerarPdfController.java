package com.example.pocapi.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pocapi.model.Formulario;
import com.example.pocapi.repository.TransferenciaRepository;
import com.example.pocapi.service.TransferenciaService;

@RestController
@RequestMapping("api")
public class GerarPdfController {
	
	void ConversaoDate2String(@RequestBody Formulario formulario, @PathVariable Long id) {
		
		// Recebe data string com formato instant dataAquisicao
		Instant dataAquisicaoInstant = Instant.parse(formulario.getDataAquisicao());
		OffsetDateTime dataAquisicaoOffSet = dataAquisicaoInstant.atOffset(ZoneOffset.UTC); 

		DateTimeFormatter formatadorAquisicao = DateTimeFormatter.ofPattern("ddMMyyyy");
		String dataAquisicaoString = dataAquisicaoOffSet.format(formatadorAquisicao);

		// Recebe data string com formato instant dataLeilao
		Instant dataLeilaoInstant = Instant.parse(formulario.getDataLeilao());
		OffsetDateTime dataLeilaoOffSet = dataLeilaoInstant.atOffset(ZoneOffset.UTC); 

		DateTimeFormatter formatadorLeilao = DateTimeFormatter.ofPattern("ddMMyyyy");
		String dataLeilaoString = dataLeilaoOffSet.format(formatadorLeilao);

		// ...
		formulario.setDataAquisicao(dataAquisicaoString);

		formulario.setDataLeilao(dataLeilaoString);
        
	}

	@Autowired
	TransferenciaService transferenciaService;

	@Autowired
	private TransferenciaRepository transferenciaRepository;
	
	/*
	@GetMapping(value = "/image")
	public @ResponseBody byte[] getImage() throws IOException {
	    InputStream in = getClass()
	      .getResourceAsStream("//home//lucas//Downloads//servicosDetran.pdf");
	    return IOUtils.toByteArray(in);
	}

	
	 @GetMapping(value = "/image", produces =
	 MediaType.APPLICATION_OCTET_STREAM_VALUE) public @ResponseBody byte[]
	 getFile() throws IOException { // final InputStream in =
	 getClass().getResourceAsStream("imagem.jpg"); ClassPathResource pdfFile = new
	 ClassPathResource("imagem.jpg"); return IOUtils.toByteArray(in); }
	 */

	@GetMapping(value = "/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Object> PegarPDF() throws IOException, InterruptedException {
		
		System.out.println("Espera 10 segundos");
        Thread.sleep(2000);

		String tmpDirectory = System.getProperty("java.io.tmpdir");
		
		String filename = (tmpDirectory + "//servicosDetran.pdf");
//		String filename = "C:\\Users\\Developer\\Downloads\\selenium\\servicosDetran.pdf";
		
		File file = new File(filename);
		   InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		headers.add("Content-Disposition", "filename=" + "transferencia.pdf");

		 ResponseEntity<Object> 
		   responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(
		      MediaType.parseMediaType("application/pdf")).body(resource);

		   return responseEntity;
	}


	@GetMapping
	public List<Formulario> ListaPlaca() {
		return transferenciaRepository.findAll();
	}

	@GetMapping("/{id}")
	public java.util.Optional<Formulario> consultar(@PathVariable Long id) {

		String tmpDirectory = System.getProperty("java.io.tmpdir");
		System.out.println("tmpDirectory: " + tmpDirectory);

		return transferenciaRepository.findById(id);

	}

	@DeleteMapping("/{id}")
// @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
//	@DeleteMapping(path = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
//	public Map<String, Object> deletar(@PathVariable Long id) {
	public Map<String, Object> deletar(@PathVariable Long id) {
		transferenciaRepository.deleteById(id);
		
//		return "A cidade com Id: " + id + " Foi deletado com sucesso";
		
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("TestoDaResposta", "O cadastro com Id: " + id + " Foi deletado com sucesso");
		return responseMap;

	}

	@PutMapping("/{id}")
	public Formulario atualizar(@RequestBody Formulario formulario, @PathVariable Long id) {

		Formulario formularioSaved = transferenciaRepository.findById(id).get();

		ConversaoDate2String(formulario, id);
		
		BeanUtils.copyProperties(formulario, formularioSaved, "id");

		return transferenciaRepository.save(formularioSaved);
	}

	@PostMapping
	public Formulario cadastrar(@RequestBody Formulario formulario) {
		
		ConversaoDate2String(formulario, null);
		
		return transferenciaRepository.save(formulario);
	}

	@PostMapping("transferencia-pdf")
	public Formulario transferenciaPdf(@RequestBody Formulario formulario) throws Exception {

		transferenciaService.transferenciaPdf(formulario);

		return transferenciaRepository.save(formulario);

	}

	@GetMapping("/debug")
	public String debug() throws InterruptedException {
		Thread.sleep(3000);

		if (Math.random() > 0.5) 
			throw new RuntimeException("Deu ruim aqui :(");
		else
			return "debug";
	}

}
