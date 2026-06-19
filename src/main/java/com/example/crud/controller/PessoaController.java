package com.example.crud.controller;

import com.example.crud.model.Pessoa;
import com.example.crud.repository.PessoaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaRepository pessoaRepository;

    public PessoaController(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @GetMapping
    public String list(Model model) {
        List<Pessoa> pessoas = pessoaRepository.findAll();
        model.addAttribute("pessoas", pessoas);
        return "list";
    }

    @GetMapping("/novo")
    public String showCreateForm(Pessoa pessoa) {
        return "form";
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
