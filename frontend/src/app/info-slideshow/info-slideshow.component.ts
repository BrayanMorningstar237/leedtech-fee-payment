import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-info-slideshow',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './info-slideshow.component.html',
  styleUrls: ['./info-slideshow.component.css']
})
export class InfoSlideshowComponent implements OnInit, OnDestroy {
  current = 0;
  progressPct = 0;
  slides = [1, 2, 3];

  private readonly DURATION = 6000;
  private readonly TICK = 60;
  private autoTimer: any;
  private progressTimer: any;

  ngOnInit()    { this.startAuto(); }
  ngOnDestroy() { this.clearTimers(); }

  goTo(i: number) { this.current = i; this.resetAuto(); }
  next() { this.current = (this.current + 1) % this.slides.length; this.resetAuto(); }
  prev() { this.current = (this.current - 1 + this.slides.length) % this.slides.length; this.resetAuto(); }

  private startAuto() {
    this.progressPct = 0;
    const step = 100 / (this.DURATION / this.TICK);
    this.progressTimer = setInterval(() => {
      this.progressPct = Math.min(this.progressPct + step, 100);
    }, this.TICK);
    this.autoTimer = setInterval(() => {
      this.current = (this.current + 1) % this.slides.length;
      this.progressPct = 0;
    }, this.DURATION);
  }

  private resetAuto() { this.clearTimers(); this.startAuto(); }
  private clearTimers() {
    clearInterval(this.autoTimer);
    clearInterval(this.progressTimer);
  }
}