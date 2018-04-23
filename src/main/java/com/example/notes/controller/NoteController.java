package com.example.notes.controller;

import com.example.notes.exception.ResourceNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController
{
    @Autowired
    NoteRepository noteRepository;

    @RequestMapping(value = "/{noteId}", method = RequestMethod.GET)
    public String note(@PathVariable(value = "noteId") long noteId, Model model){
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));
        model.addAttribute("note", note);
        return "/";
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model){
        List<Note> notes = noteRepository.findAllByOrderByCreatedAtDesc();
        if(!model.containsAttribute("newNote")) {
            model.addAttribute("newNote", new Note());
        }
        model.addAttribute("notes", notes);
        return "index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createNote(@Valid Note newNote, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newNote", bindingResult);
            redirectAttributes.addFlashAttribute("newNote", newNote);
            redirectAttributes.addFlashAttribute("flash_message", "Could not create new note. Try again.");
            return "redirect:/notes/";
        }
        noteRepository.save(newNote);
        redirectAttributes.addFlashAttribute("flash_message", "Successful creation");
        return "redirect:/notes/";
    }


    @GetMapping("/{noteId}/edit")
    public String edit_note(@PathVariable(value = "noteId") Long noteId, Model model){
        Note editedNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));
        if(!model.containsAttribute("editedNote")) {
            model.addAttribute("editedNote", editedNote);
        }
        return "edit";
    }


    @RequestMapping(value = "/{noteId}/update", method = RequestMethod.POST)
    public String updateNote(@PathVariable(value = "noteId") Long noteId,
                             @Valid Note editedNote, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        Note updatedNote = noteRepository.findById(noteId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));
        editedNote.setId(noteId);
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editedNote", bindingResult);
            redirectAttributes.addFlashAttribute("editedNote", editedNote);
            redirectAttributes.addFlashAttribute("flash_message", "an error has occurred and your changes has not been saved!");
            return String.format("redirect:/notes/%s/edit", noteId);
        }
        updatedNote.setTitle(editedNote.getTitle());
        updatedNote.setContent(editedNote.getContent());
        noteRepository.save(updatedNote);
        redirectAttributes.addFlashAttribute("flash_message", "Note has been updated.");
        return String.format("redirect:/notes/%s/edit", noteId);
    }

    @RequestMapping(value = "/{noteId}/delete", method = RequestMethod.POST)
    public String deleteNote(@PathVariable(value = "noteId") Long noteId, RedirectAttributes redirectAttributes){
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        noteRepository.delete(note);
        redirectAttributes.addFlashAttribute("flash_message", "Note has been deleted.");
        return String.format("redirect:/notes/", noteId);
    }
}




