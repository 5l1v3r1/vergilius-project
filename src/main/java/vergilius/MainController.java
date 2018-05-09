package vergilius;

import java.io.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import vergilius.repos.TdataRepository;
import vergilius.repos.OsRepository;
import vergilius.repos.TtypeRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
public class MainController{
    @Autowired
    public OsRepository rep1;
    @Autowired
    public TtypeRepository rep2;
    @Autowired
    public TdataRepository rep3;

    @GetMapping("/login")
    public String displayLogin(Model model) throws IOException {
        model.addAttribute("os", getListOs());
        return "login";
    }
    @PostMapping("/login")
    public String handleLogin(@RequestParam(name="username") String username, @RequestParam(name="password") String password, HttpSession session, Model model) throws IOException {
        model.addAttribute(username);
        model.addAttribute(password);
        model.addAttribute("os", getListOs());
        return "login";
    }

    @GetMapping("/admin")
    public String displayAdmin(Model model) throws IOException {
        model.addAttribute("os", getListOs());
        return "admin";
    }

    @PostMapping("/admin")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        try(InputStream res = file.getInputStream()) {
/*
            Yaml yaml = new Yaml();
            yaml.setBeanAccess(BeanAccess.FIELD);
            RootOs fromYaml = yaml.loadAs(res, RootOs.class);
            List<Os> mylist = fromYaml.getOpersystems();
            rep1.save(mylist);
*/
/*
            Yaml yaml = new Yaml();
            yaml.setBeanAccess(BeanAccess.FIELD);
            Root fromYaml = yaml.loadAs(res, Root.class);

            List<Ttype> obj = fromYaml.getTypes();

            for(int i = 0; i < obj.size(); i++)
            {
                Set<Tdata> tmp = obj.get(i).getData();

                if(tmp != null)
                {
                    Iterator<Tdata> iter = tmp.iterator();
                    while (iter.hasNext()) {
                        Tdata record = iter.next();
                        record.setTtype(obj.get(i));
                    }
                }
            }
            rep2.save(obj);
*/

        }
        catch(IOException e){}

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/admin";
    }

    public List<Os> getListOs()
    {
        List<Os> listOfOperSystems = new ArrayList<>();
        for(Os i : rep1.findAll())
        {
            listOfOperSystems.add(i);
        }
        return listOfOperSystems;
    }
    @GetMapping("/")
    public String displayHome(Model model)
    {
        model.addAttribute("os", getListOs());
        return "home";
    }

    @GetMapping("/statistics")
    public String displayStatistics(Model model)
    {
        model.addAttribute("os", getListOs());
        return "statistics";
    }

    @GetMapping("/kernels")
    public String displayKernels(Model model)
    {
       model.addAttribute("os", getListOs());
       return "kernels";
    }

    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String logoutPage(Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("os", getListOs());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "/";
    }
    @GetMapping("/about")
    public String displayAbout(Model model)
    {
        model.addAttribute("os", getListOs());
        return "about";
    }

    @RequestMapping(value = "/os/{osname:.+}", method = RequestMethod.GET)
    public String displayKinds(@PathVariable String osname, Model model)
    {
        Os opersys = rep1.findByOsname(osname);
        List<Ttype> reslist = rep2.findByOpersysAndIsConstFalseAndIsVolatileFalse(opersys);

        model.addAttribute("structs", Ttype.FilterByTypes(reslist, Ttype.Kind.STRUCT));
        model.addAttribute("unions", Ttype.FilterByTypes(reslist, Ttype.Kind.UNION));
        model.addAttribute("enums", Ttype.FilterByTypes(reslist, Ttype.Kind.ENUM));

        model.addAttribute("os", getListOs());

        return "ttype";
    }

    @RequestMapping(value = "/os/{osname:.+}/type/{name}", method = RequestMethod.GET)
    public String displayType(@PathVariable String osname,@PathVariable String name, Model model)
    {
        Os opersys = rep1.findByOsname(osname);
        List<Ttype> typeslist = rep2.findByNameAndOpersysAndIsConstFalseAndIsVolatileFalse(name, opersys);

        String link = "/os/" + osname + "/type/";
        for(int i = 0; i < typeslist.size(); i++)
        {
            if(typeslist.get(i).getName().equals(name))
            {
                model.addAttribute("ttype", FieldBuilder.recursionProcessing(rep2, typeslist.get(i),0, 0, link).toString());
            }
        }
        model.addAttribute("os", getListOs());

        return "tdata";
    }

}

