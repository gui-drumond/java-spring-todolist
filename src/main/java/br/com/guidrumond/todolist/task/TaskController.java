package br.com.guidrumond.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.guidrumond.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
 
    @Autowired //Para o Spring boot gerenciar a instância da Class 
    private ITaskRepository taskRepository;

    @PostMapping("/") // Mapemento de rotas

    // ResponseEntity serve para definir a forma que sera feita a reposta da req, com status code e body

    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        var idUser = request.getAttribute("idUser"); // pega o usuário da request

        taskModel.setIdUser((UUID) idUser); // adiciona o atributo
        

        var currentDate = LocalDateTime.now();        
        var startAt = LocalDateTime.from(taskModel.getStartAt());  
        var endAt = LocalDateTime.from(taskModel.getEndAt());


        if(currentDate.isAfter(startAt) || currentDate.isAfter(endAt)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de inicio / data de termino deve ser maior que a atual!"); 
        }

        if(startAt.isAfter(endAt)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de inicio deve ser menor do que a data de termino!"); 
        }

        var task = this.taskRepository.save(taskModel); // envia para o banco de dados

        return ResponseEntity.status(HttpStatus.OK).body(task); 
    }


    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var userId = request.getAttribute("idUser");
        return this.taskRepository.findByIdUser((UUID) userId);
    } 


    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id ){
        var task = this.taskRepository.findById(id).orElse(null);
        var userId = request.getAttribute("idUser");
        
        if(task != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada! "); 
        }
        

        if(!task.getIdUser().equals(userId)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para alterar essa tarefa"); 
        }

        Utils.copyNonNullProperties(taskModel, task);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    } 
}
