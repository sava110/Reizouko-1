package com.example.demo.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Reizouko;
import com.example.demo.model.ReizoukoRoom;
import com.example.demo.model.Shokuhin;

@Controller
public class MainController {

    private final List<Reizouko> reizoukos = new ArrayList<>();
    private final List<Shokuhin> foods = new ArrayList<>();

    public MainController() {
        // サンプル食品
        Shokuhin apple = new Shokuhin("りんご", "果物", 3, "個", "2025-07-15");
        Shokuhin milk = new Shokuhin("牛乳", "飲み物", 1, "本", "2025-07-12");

        List<ReizoukoRoom> roomsA = new ArrayList<>();
        roomsA.add(new ReizoukoRoom(1, "野菜室", new ArrayList<>(List.of(apple))));
        roomsA.add(new ReizoukoRoom(2, "冷蔵室", new ArrayList<>(List.of(milk))));

        List<ReizoukoRoom> roomsB = new ArrayList<>();
        roomsB.add(new ReizoukoRoom(3, "冷蔵室", new ArrayList<>()));
        roomsB.add(new ReizoukoRoom(4, "冷凍室", new ArrayList<>()));


        reizoukos.add(new Reizouko(1, "自宅の冷蔵庫", roomsA));
        reizoukos.add(new Reizouko(2, "職場の冷蔵庫", roomsB));
    }

    // === 冷蔵庫一覧表示 ===
    @GetMapping("/reizoukos")
    public String showReizoukoList(Model model) {
        model.addAttribute("reizoukos", reizoukos);
        return "reizouko_list";
    }
    
    @PostMapping("/reizouko/add")
    public String addReizouko(@RequestParam String name) {
        int newId = reizoukos.stream().mapToInt(Reizouko::getId).max().orElse(0) + 1;
        reizoukos.add(new Reizouko(newId, name, new ArrayList<>()));
        return "redirect:/reizoukos";
    }

    private int nextRoomId = 100; // 任意の初期値

    @PostMapping("/reizouko/{id}/room/add")
    public String addRoom(@PathVariable int id, @RequestParam String roomName) {
        Reizouko rz = reizoukos.stream()
            .filter(r -> r.getId() == id)
            .findFirst()
            .orElse(null);

        if (rz == null) return "redirect:/reizoukos";

        // 🔽【ここ】部屋名の重複チェック
        boolean exists = rz.getRooms().stream()
            .anyMatch(r -> r.getRoomName().equals(roomName));

        if (exists) {
            return "redirect:/reizouko/" + id + "?error=roomExists";
        }

        // IDの重複を避けるため、グローバルカウンター or UUID
        int newRoomId = rz.getRooms().stream()
            .mapToInt(ReizoukoRoom::getId)
            .max()
            .orElse(0) + 1;

        rz.getRooms().add(new ReizoukoRoom(newRoomId, roomName, new ArrayList<>()));

        return "redirect:/reizouko/" + id;
    }


    
    @PostMapping("/reizouko/{id}/food/add")
    public String addFoodToRoom(@PathVariable int id,
                                @RequestParam String room,
                                @RequestParam String name,
                                @RequestParam String genre,
                                @RequestParam int amount,
                                @RequestParam String unit,
                                @RequestParam String expiryDate) {

        Reizouko rz = reizoukos.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        if (rz == null) return "redirect:/reizoukos";

        ReizoukoRoom targetRoom = rz.getRooms().stream()
            .filter(r -> r.getRoomName().equals(room))
            .findFirst()
            .orElse(null);

        if (targetRoom != null) {
            targetRoom.getFoods().add(new Shokuhin(name, genre, amount, unit, expiryDate));
        }

        String encodedRoom = URLEncoder.encode(room, StandardCharsets.UTF_8);
        return "redirect:/reizouko/" + id + "?room=" + encodedRoom;
    }



    // === 冷蔵庫詳細（部屋と食品） ===
    @GetMapping("/reizouko/{id}")
    public String showReizoukoDetail(@PathVariable int id,
                                     @RequestParam(defaultValue = "冷蔵室") String room,
                                     Model model) {
        Reizouko rz = reizoukos.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        if (rz == null) return "redirect:/reizoukos";

        ReizoukoRoom selectedRoom = rz.getRooms().stream()
            .filter(r -> r.getRoomName().equals(room))
            .findFirst()
            .orElse(null);

        List<Shokuhin> roomFoods = selectedRoom != null ? selectedRoom.getFoods() : new ArrayList<>();

        model.addAttribute("reizouko", rz);
        model.addAttribute("rooms", rz.getRooms());
        model.addAttribute("selectedRoom", room);
        model.addAttribute("foods", roomFoods);
        model.addAttribute("newFood", new Shokuhin());

        return "reizouko_detail";
    }

    // === 食品追加（重複チェック付き） ===
    @PostMapping("/reizouko/{id}/add")
    public String addItem(@PathVariable int id,
                          @RequestParam String room,
                          @ModelAttribute Shokuhin newFood) {

        if (newFood.getName() == null || newFood.getExpiryDate() == null) {
            return "redirect:/reizouko/" + id + "?room=" + room;
        }

        Reizouko rz = reizoukos.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        if (rz == null) return "redirect:/reizoukos";

        ReizoukoRoom targetRoom = rz.getRooms().stream()
            .filter(r -> r.getRoomName().equals(room))
            .findFirst()
            .orElse(null);

        if (targetRoom != null) {
            for (Shokuhin food : targetRoom.getFoods()) {
                if (food.getName().equals(newFood.getName())
                    && food.getExpiryDate().equals(newFood.getExpiryDate())
                    && food.getUnit().equals(newFood.getUnit())) {

                    food.setAmount(food.getAmount() + newFood.getAmount());
                    return "redirect:/reizouko/" + id + "?room=" + room;
                }
            }
            targetRoom.getFoods().add(newFood);
        }

        return "redirect:/reizouko/" + id + "?room=" + room;
    }
    
    @PostMapping("/reizouko/{id}/delete")
    public String deleteReizouko(@PathVariable int id) {
        reizoukos.removeIf(r -> r.getId() == id);
        return "redirect:/reizoukos";
    }

    
    // === 食品削除 ===
    @PostMapping("/reizouko/{id}/delete-item")
    public String deleteItem(@PathVariable int id,
                             @RequestParam String room,
                             @RequestParam int foodId) {
        Reizouko rz = reizoukos.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        if (rz == null) return "redirect:/reizoukos";

        ReizoukoRoom targetRoom = rz.getRooms().stream()
            .filter(r -> r.getRoomName().equals(room))
            .findFirst()
            .orElse(null);

        if (targetRoom != null) {
            targetRoom.getFoods().removeIf(f -> f.getId() == foodId);
        }

        return "redirect:/reizouko/" + id + "?room=" + URLEncoder.encode(room, StandardCharsets.UTF_8);

    }


    // === 食品数量の増減 ===
    @PostMapping("/reizouko/{id}/update")
    public String updateQuantity(@PathVariable int id,
                                 @RequestParam String room,
                                 @RequestParam int foodId,
                                 @RequestParam String action) {
        Reizouko rz = reizoukos.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        if (rz == null) return "redirect:/reizoukos";

        ReizoukoRoom targetRoom = rz.getRooms().stream()
            .filter(r -> r.getRoomName().equals(room))
            .findFirst()
            .orElse(null);

        if (targetRoom != null) {
            for (Shokuhin food : targetRoom.getFoods()) {
                if (food.getId() == foodId) {
                    int amount = food.getAmount();
                    if ("increment".equals(action)) {
                        food.setAmount(amount + 1);
                    } else if ("decrement".equals(action) && amount > 0) {
                        food.setAmount(amount - 1);
                    }
                    break;
                }
            }
        }

        return "redirect:/reizouko/" + id + "?room=" + room;
    }
}
