package vergilius;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
        List<Os> os = getListOs();

        model.addAttribute("os", os);
        model.addAttribute("families", getListOfFamilies(os));

        return "login";
    }
    @PostMapping("/login")
    public String handleLogin(@RequestParam(name="username") String username, @RequestParam(name="password") String password, HttpSession session, Model model) throws IOException {
        model.addAttribute(username);
        model.addAttribute(password);

        List<Os> os = getListOs();

        model.addAttribute("os", os);
        model.addAttribute("families", getListOfFamilies(os));

        return "login";
    }

    @GetMapping("/admin")
    public String displayAdmin(Model model) throws IOException {

        List<Os> os = getListOs();

        model.addAttribute("os", os);
        model.addAttribute("families", getListOfFamilies(os));

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

    public List<String> getListOfFamilies(List<Os> opers)
    {
        List<String> fam = new ArrayList<>(); // set allows only unique elements -> CHANGE LATER
        for(Os i: opers)
        {
            if(!fam.contains(i.getFamily()))
            {
                fam.add(i.getFamily());
            }
        }
        return fam;
    }

    @GetMapping("/")
    public String displayHome(Model model)
    {
        List<Os> os = getListOs();
        //kostyl na vremya
        /*
        for(Os i: os)
        {
           i.setConverted_date(i.convertTimestamptoDate(i.getTimestamp()));
        }
        rep1.save(os);
        */
        model.addAttribute("os", os);
        model.addAttribute("families", getListOfFamilies(os));

        return "home";
    }

    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String logoutPage(Model model, HttpServletRequest request, HttpServletResponse response) {

        List<Os> os = getListOs();

        model.addAttribute("os", os);
        model.addAttribute("families", getListOfFamilies(os));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "/";
    }
    @GetMapping("/about")
    public String displayAbout(Model model)
    {
        List<Os> os = getListOs();

        model.addAttribute("os", os);
        model.addAttribute("families", getListOfFamilies(os));

        return "about";
    }

    @GetMapping("/kernels")
    public String displaySpace(Model model)
    {
        List<Os> os = getListOs();

        model.addAttribute("os", os);
        model.addAttribute("families", getListOfFamilies(os));

        return "kernels";
    }

    @RequestMapping(value="/kernels/{famname:.+}")
    public String displayFamily(@PathVariable String famname, Model model)
    {
        List<Os> fam = rep1.findByFamily(famname);

        List<Os> os = getListOs();

        model.addAttribute("os", os);
        model.addAttribute("families", getListOfFamilies(os));

        model.addAttribute("fam", fam);

        return "family";
    }

    @RequestMapping(value = "/os/{osname:.+}", method = RequestMethod.GET)
    public String displayKinds(@PathVariable String osname, Model model)
    {
        Os opersys = rep1.findByOsname(osname);
        List<Ttype> reslist = rep2.findByOpersysAndIsConstFalseAndIsVolatileFalse(opersys);

        model.addAttribute("structs", Ttype.FilterByTypes(reslist, Ttype.Kind.STRUCT));
        model.addAttribute("unions", Ttype.FilterByTypes(reslist, Ttype.Kind.UNION));
        model.addAttribute("enums", Ttype.FilterByTypes(reslist, Ttype.Kind.ENUM));

        model.addAttribute("osfam", rep1.findFamilyByOsname(osname));

        List<Os> os = getListOs();

        model.addAttribute("os", os);
        model.addAttribute("families", getListOfFamilies(os));

        return "ttype";
    }

    @RequestMapping(value = "/os/{osname:.+}/type/{name}", method = RequestMethod.GET)
    public String displayType(@PathVariable String osname,@PathVariable String name, Model model)
    {
        Os opersys = rep1.findByOsname(osname);

        String link = "/os/" + osname + "/type/";

        List<Ttype> typeslist = rep2.findByNameAndOpersys(name, opersys);

        //NOT EMPTY?
        if(typeslist != null && !typeslist.isEmpty())
        {
            model.addAttribute("ttype", FieldBuilder.recursionProcessing(rep2, typeslist.get(0), 0, 0, link).toString());

            //search for cross-links
            List<Ttype> used_in = new ArrayList<>();

            for(Ttype i: typeslist)
            {
                used_in.addAll(rep2.findById1(i.getIdtype()));
                used_in.addAll(rep2.findById2(i.getIdtype()));
                used_in.addAll(rep2.findById3(i.getIdtype()));
                used_in.addAll(rep2.findById4(i.getIdtype()));
            }

            List<String> used_in_names = new ArrayList<>();
            for (Ttype i : used_in)
            {
                used_in_names.add(i.getName());
            }

            if (!used_in_names.isEmpty())
            {
                Stream<String> stream = used_in_names.stream().distinct();
                used_in_names = stream.sorted().collect(Collectors.toList());
            }
            else
            {
                used_in_names = null;
            }
            model.addAttribute("cros", used_in_names);
            //System.out.println(used_in_names.size());
        }
        List<Os> os = getListOs();

        //change "8" on size
        Map<String, Integer> map = new HashMap<>();
        Map<Integer, String> mapInverted = new HashMap<>();
        for(int i = 1; i <= 8; i++)
        {
            map.put(os.get(i - 1).getOsname(), i);
            mapInverted.put(i, os.get(i - 1).getOsname());
        }

        model.addAttribute("osfam", rep1.findFamilyByOsname(osname));

        model.addAttribute("os", os);
        model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("mapos", map);
        model.addAttribute("invertMapos", mapInverted);

        return "tdata";
    }
}

