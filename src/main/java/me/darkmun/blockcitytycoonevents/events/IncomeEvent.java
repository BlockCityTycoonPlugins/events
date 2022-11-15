package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.Config;

public interface IncomeEvent extends BlockCityTycoonEvent {
    double getRealIncome();
    void setRealIncome(double income);
}
