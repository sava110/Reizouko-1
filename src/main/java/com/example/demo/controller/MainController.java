package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Shokuhin;

@Controller
public class MainController {

    private List<Shokuhin> foods = new ArrayList<>();
    
    private boolean initialized = false;

    private void initData() {
        if (!initialized) {
            foods.add(new Shokuhin("鶏肉", "肉類", 1, "個", "2025-07-10"));
            foods.add(new Shokuhin("鮭", "魚介類", 2, "本", "2025-07-15"));
            foods.add(new Shokuhin("にんじん", "野菜", 3, "本", "2025-07-12"));
            foods.add(new Shokuhin("牛乳", "飲み物", 1, "本", "2025-07-18"));
            foods.add(new Shokuhin("バナナ", "果物", 3, "本", "2025-07-20"));
            initialized = true;
        }
    }


    @GetMapping("/list")
    public String listFoods(@RequestParam(required = false) String genre, Model model) {
        initData();  // 初期化は一度だけ

        List<String> genreOrder = List.of(
            "肉類", "魚介類", "野菜", "果物", "飲み物",
            "乳製品・卵", "調味料", "加工食品・惣菜", "穀類・主食", "スイーツ・デザート"
        );

        List<Shokuhin> filteredFoods;
        if (genre == null || genre.isEmpty()) {
            filteredFoods = new ArrayList<>(foods);

            // ★ 2段階ソート：カテゴリ → 賞味期限
            filteredFoods.sort(
                Comparator
                    .comparingInt((Shokuhin f) -> genreOrder.indexOf(f.getGenre()))
                    .thenComparing(f -> LocalDate.parse(f.getExpiryDate()))
            );
        } else {
            filteredFoods = foods.stream()
                .filter(food -> genre.equals(food.getGenre()))
                .sorted(Comparator.comparing(f -> LocalDate.parse(f.getExpiryDate())))
                .toList();
        }

        model.addAttribute("foods", filteredFoods);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("genreList", genreOrder);
        model.addAttribute("newFood", new Shokuhin());
        return "list";
    }





    @PostMapping("/update")
    public String updateQuantity(@RequestParam int id, @RequestParam String action) {
        for (Shokuhin food : foods) {
            if (food.getId() == id) {
                double amount = food.getAmount();
                if ("increment".equals(action)) {
                    food.setAmount(amount + 1);
                } else if ("decrement".equals(action) && amount > 0) {
                    food.setAmount(amount - 1);
                }
                break;
            }
        }
        return "redirect:/list";
    }



    @PostMapping("/delete")
    public String deleteItem(@RequestParam int index) {
        if (index >= 0 && index < foods.size()) {
            foods.remove(index);
        }
        return "redirect:/list";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Shokuhin newFood) {
        foods.add(newFood);
        return "redirect:/list";
    }
}
