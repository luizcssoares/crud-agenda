package com.example.crud.controller;

import com.example.crud.model.Mensagem;
import com.example.crud.model.Pessoa;
import com.example.crud.repository.PessoaRepository;
import com.example.crud.service.PythonService;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Controller
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaRepository pessoaRepository;
    private final PythonService pythonService;

    public PessoaController(PessoaRepository pessoaRepository, PythonService pythonService) {
        this.pessoaRepository = pessoaRepository;
        this.pythonService = pythonService;
    }

    @GetMapping
    public String list(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Pessoa> pessoaPage = pessoaRepository.findAll(pageable);
        model.addAttribute("pessoaPage", pessoaPage);
        model.addAttribute("pessoas", pessoaPage.getContent());
        return "list";
    }

    @GetMapping("/novo")
    public String showCreateForm(Pessoa pessoa) {
        return "form";
    }

    @GetMapping("/whatszap")
    public String sendMessage(Model model) {
        Mensagem retorno = pythonService.enviaMensagem();
        model.addAttribute("mensagem", retorno);
        return "whatszap";
    }

    @GetMapping("/hello")
    public String showMessage(Model model) {
        Mensagem retorno = pythonService.buscarMensagem();
        model.addAttribute("mensagem", retorno);
        return "hello";
    }

    @PostMapping
    public String create(@Valid Pessoa pessoa, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "form";
        }
        pessoaRepository.save(pessoa);
        return "redirect:/pessoas";
    }

    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable("id") long id, Model model) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa inválida Id:" + id));
        model.addAttribute("pessoa", pessoa);
        return "form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") long id, @Valid Pessoa pessoa, BindingResult result, Model model) {
        if (result.hasErrors()) {
            pessoa.setId(id);
            return "form";
        }
        pessoaRepository.save(pessoa);
        return "redirect:/pessoas";
    }

    @GetMapping("/remover/{id}")
    public String delete(@PathVariable("id") long id, Model model) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa inválida Id:" + id));
        pessoaRepository.delete(pessoa);
        return "redirect:/pessoas";
    }
}
