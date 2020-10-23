package com.example.pocapi.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


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
	public ResponseEntity<Object> PegarPDF() throws IOException {

//		ClassPathResource file = new ClassPathResource("servicosDetran.pdf");
//		Resource file = (Resource) resourceLoader.getResource("C:\\Users\\Developer\\Downloads\\selenium\\servicosDetran.pdf");
//		InputStream inputStream = ((ClassPathResource) file).getInputStream();
		
//		String filename = downloadFilePath;

		String tmpDirectory = System.getProperty("java.io.tmpdir");
		
		String filename = "C:\\Users\\Developer\\Downloads\\selenium\\servicosDetran.pdf";
		File file = new File(filename);
		   InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		headers.add("Content-Disposition", "filename=" + "transferencia.pdf");

		 ResponseEntity<Object> 
		   responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(
		      MediaType.parseMediaType("application/txt")).body(resource);

		   return responseEntity;

//		ResponseEntity<InputStreamResource> response = new ResponseEntity<InputStreamResource>(
//				new InputStreamResource(((ClassPathResource) file).getInputStream()), headers, HttpStatus.OK);
//		return response;
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
	public String deletar(@PathVariable Long id) {
		transferenciaRepository.deleteById(id);
		return "A cidade com Id: " + id + " Foi deletado com sucesso";
	}

	@PutMapping("/{id}")
	public Formulario atualizar(@RequestBody Formulario formulario, @PathVariable Long id) {
		Formulario formularioSaved = transferenciaRepository.findById(id).get();

		BeanUtils.copyProperties(formulario, formularioSaved, "id");

		return transferenciaRepository.save(formularioSaved);
	}
	
	@PostMapping
	public Formulario cadastrar(@RequestBody Formulario formulario) {
		return transferenciaRepository.save(formulario);
	}

	@PostMapping("transferencia-pdf")
	public Formulario transferenciaPdf(@RequestBody Formulario formulario) throws Exception {

		transferenciaService.transferenciaPdf(formulario);

		return transferenciaRepository.save(formulario);

	}

}
